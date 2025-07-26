package com.community_service_hub.notification_service.serviceImpl;

import com.community_service_hub.notification_service.dto.OTPPayload;
import com.community_service_hub.notification_service.models.OTP;
import com.community_service_hub.notification_service.repo.OTPRepo;
import com.community_service_hub.notification_service.service.OTPService;
import com.community_service_hub.user_service.dto.ResponseDTO;
import com.community_service_hub.user_service.exception.BadRequestException;
import com.community_service_hub.user_service.exception.NotFoundException;
import com.community_service_hub.user_service.exception.ServerException;
import com.community_service_hub.user_service.models.NGO;
import com.community_service_hub.user_service.models.User;
import com.community_service_hub.user_service.repo.NGORepo;
import com.community_service_hub.user_service.repo.UserRepo;
import com.community_service_hub.user_service.util.AppUtils;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.random.RandomGenerator;

@Slf4j
@Service
public class OTPServiceImpl implements OTPService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final UserRepo userRepo;
    private final OTPRepo otpRepo;
    private final NGORepo ngoRepo;

    @Autowired
    public OTPServiceImpl(JavaMailSender mailSender, TemplateEngine templateEngine, UserRepo userRepo, OTPRepo otpRepo, NGORepo ngoRepo) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.userRepo = userRepo;
        this.otpRepo = otpRepo;
        this.ngoRepo = ngoRepo;
    }

    /**
     * @description This method is used to send an otp code to a user given the required payload.
     * @param otpPayload
     * @return
     * @auther Emmanuel Yidana
     * @createdAt 10h May 2025
     */
    @Override
    public void sendOtp(OTPPayload otpPayload) {
        try {
            log.info("In send otp method:->>>>>>");
            Optional<User> user = userRepo.findUserByEmail(otpPayload.getEmail());
            NGO ngo = ngoRepo.findByEmail(otpPayload.getEmail());

            if (user.isEmpty() && ngo==null){
                throw new NotFoundException("user record not found to send email");
            }

            /**
             * check if user have a existing otp. delete it if exist before sending a new one.
             */
            OTP otpExist = otpRepo.findByUserId(user.isPresent()?user.get().getId():ngo.getId());
            if (otpExist != null){
               otpRepo.deleteById(otpExist.getId());
            }

            /**
             * setting email items
             */
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setSubject("OTP Verification");
            helper.setFrom("eyidana001@gmail.com");
            helper.setTo(otpPayload.getEmail());

            /**
             * setting variables values to passed to the template
             */
            Context context = new Context();
            otpPayload.setOtpCode(generateOTP());
            context.setVariable("otp", otpPayload.getOtpCode());
            context.setVariable("fullName", user.isPresent()?user.get().getName():ngo.getOrganizationName());

            String htmlContent = templateEngine.process("OTPTemplate", context);
            helper.setText(htmlContent, true);

            OTP otp = saveOTP(otpPayload);
            if (otp == null){
                throw new BadRequestException("fail to save otp record");
            }

            log.info("Otp sent:->>>");
            mailSender.send(message);

        } catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            throw new ServerException("Internal server error!");
        }
    }

    /**
     * @description a helper method for otp record to the db
     * @param otpPayload
     * @return
     */
    public OTP saveOTP(OTPPayload otpPayload){
        Optional<User> user = userRepo.findUserByEmail(otpPayload.getEmail());
        NGO ngo = ngoRepo.findByEmail(otpPayload.getEmail());

        if (user.isEmpty() && ngo==null){
            throw new NotFoundException("user record not found to send email");
        }

        OTP otp = new OTP();
        otp.setOtpCode(otpPayload.getOtpCode());
        otp.setStatus(false);
        otp.setExpireAt(ZonedDateTime.now().plusMinutes(2));
        otp.setUserId(user.isPresent()?user.get().getId():ngo.getId());
        return otpRepo.save(otp);
    }

    /**
     * @description This method is used to verify user otp.
     * @param otpPayload
     * @return
     * @auther Emmanuel Yidana
     * @createdAt 10h May 2025
     */
    @Override
    public ResponseEntity<ResponseDTO> verifyOtp(OTPPayload otpPayload) {
       try {
           /**
            * checking if user exist
            */
           Optional<User> user = userRepo.findUserByEmail(otpPayload.getEmail());
           NGO ngo = ngoRepo.findByEmail(otpPayload.getEmail());

           if (user.isEmpty() && ngo==null){
               throw new NotFoundException("user record not found to send email");
           }

           /**
            * check if otp exist
            */
           OTP otpExist = otpRepo.findByUserId(user.isPresent()?user.get().getId():ngo.getId());
           if (otpExist == null){
               ResponseDTO response = AppUtils.getResponseDto("OTP record not found", HttpStatus.NOT_FOUND);
               return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
           }

           /**
            * check if the otp does not expire
            */
           if (!ZonedDateTime.now().isBefore(otpExist.getExpireAt())){
               ResponseDTO response = AppUtils.getResponseDto("OTP has expired", HttpStatus.BAD_REQUEST);
               return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
           }

           /**
            * check if otp entered by user match the one in the db
            */
           if (otpPayload.getOtpCode() != otpExist.getOtpCode()){
               ResponseDTO response = AppUtils.getResponseDto("OTP do not match", HttpStatus.BAD_REQUEST);
               return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
           }

           /**
            * remove otp after verification
            */
           otpExist.setStatus(true);
           otpRepo.deleteById(otpExist.getId());

           ResponseDTO response = AppUtils.getResponseDto("OTP verified", HttpStatus.OK);
           return new ResponseEntity<>(response, HttpStatus.OK);
       } catch (Exception e) {
           throw new ServerException(e.getMessage());
       }
    }

    /**
     * @description a helper method to generate otp codes
     * @return returns integer value of the generated otp code
     * @auther Emmanuel Yidana
     */
    public Integer generateOTP(){
        RandomGenerator generator = new Random();
        return generator.nextInt(200001, 900000);
    }

    /**
     * @description a helper method to check user verification status during login
     * @param email
     * @return true if user is verified otherwise return false
     * @auther Emmanuel Yidana
     */
    public Boolean checkOTPStatusDuringLogin(String email){

        Optional<User> userOptional = userRepo.findUserByEmail(email);
        Optional<NGO> ngoOptional = ngoRepo.findNGOByEmail(email);

        if (ngoOptional.isEmpty()&&userOptional.isEmpty()){
            throw new NotFoundException("User record not found");
        }

        OTP otpExist = otpRepo.findByUserId(userOptional.isPresent()?userOptional.get().getId():ngoOptional.get().getId());

        return otpExist == null;
    }


    /**
     * @description This method is used to send a password reset link to a user given the required payload.
     * @param email the email of the user
     * @auther Emmanuel Yidana
     * @createdAt 22nd July 2025
     */
    public void sendResetPasswordLink(String email){
        try {

            /**
             * loading the user data from the db by the user email
             */
            Optional<User> user = userRepo.findUserByEmail(email);

            if (user.isEmpty()){
                log.info("no user email provide with the email provided->>>{}", email);
                throw new NotFoundException("no user record found with the email provide");
            }

            /**
             * setting email items
             */
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setSubject("Password Reset");
            helper.setFrom("eyidana001@gmail.com");
            helper.setTo(email);

            /**
             * setting variables values to passed to the template
             */
            Context context = new Context();
            context.setVariable("resetLink", "http://localhost:3000/reset-password?email="+email);
            context.setVariable("fullName", user.get().getName());

            String htmlContent = templateEngine.process("PasswordResetTemplate", context);
            helper.setText(htmlContent, true);

            log.info("Otp sent to:->>>{}", email);
            mailSender.send(message);

        } catch (Exception e) {
            log.info("Message->>>{}", e.getMessage());
            throw new ServerException("error occurred while trying to send password reset link");
        }
    }
}
