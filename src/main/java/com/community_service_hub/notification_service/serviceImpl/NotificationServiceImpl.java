package com.community_service_hub.notification_service.serviceImpl;

import com.community_service_hub.notification_service.dto.ApplicationConfirmationDTO;
import com.community_service_hub.notification_service.dto.OTPPayload;
import com.community_service_hub.notification_service.models.OTP;
import com.community_service_hub.notification_service.repo.NotificationRepo;
import com.community_service_hub.notification_service.service.NotificationService;
import com.community_service_hub.user_service.dto.ResponseDTO;
import com.community_service_hub.exception.BadRequestException;
import com.community_service_hub.exception.NotFoundException;
import com.community_service_hub.exception.ServerException;
import com.community_service_hub.user_service.models.NGO;
import com.community_service_hub.user_service.models.User;
import com.community_service_hub.user_service.repo.NGORepo;
import com.community_service_hub.user_service.repo.UserRepo;
import com.community_service_hub.util.AppConstants;
import com.community_service_hub.util.AppUtils;
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
public class NotificationServiceImpl implements NotificationService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final UserRepo userRepo;
    private final NotificationRepo notificationRepo;
    private final NGORepo ngoRepo;

    @Autowired
    public NotificationServiceImpl(JavaMailSender mailSender, TemplateEngine templateEngine, UserRepo userRepo, NotificationRepo notificationRepo, NGORepo ngoRepo) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.userRepo = userRepo;
        this.notificationRepo = notificationRepo;
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
            log.info("In send otp method:->>>{}", otpPayload);
            Optional<User> user = userRepo.findUserByEmail(otpPayload.getEmail());
            NGO ngo = ngoRepo.findByEmail(otpPayload.getEmail());

            if (user.isEmpty() && ngo==null){
                throw new NotFoundException("user record not found to send email");
            }

            /**
             * check if user have a existing otp. delete it if exist before sending a new one.
             */
            OTP otpExist = notificationRepo.findByUserId(user.isPresent()?user.get().getId():ngo.getId());
            if (otpExist != null){
               notificationRepo.deleteById(otpExist.getId());
            }

            log.info("About to send otp to:->>>{}", otpPayload.getEmail());
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

            log.info("Otp sent to:->>>{}", otpPayload.getEmail());
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
        return notificationRepo.save(otp);
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
           OTP otpExist = notificationRepo.findByUserId(user.isPresent()?user.get().getId():ngo.getId());
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
           notificationRepo.deleteById(otpExist.getId());

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

        OTP otpExist = notificationRepo.findByUserId(userOptional.isPresent()?userOptional.get().getId():ngoOptional.get().getId());

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
                log.info("No user found with the email provided->>>{}", email);
                throw new NotFoundException("No user record found with the email provide");
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

            log.info("Password rest link sent to:->>>{}", email);
            mailSender.send(message);

        } catch (Exception e) {
            log.info("Message->>>{}", e.getMessage());
            throw new ServerException("error occurred while trying to send password reset link");
        }
    }

    /**
     * @description this method is used to notify user either on
     * Application Rejection, Application Approval, Application Confirmation
     * @param confirmationDTO the payload to be sent to the notification template
     * @atuher Emmanuel Yidana
     * @createdAt 31 August 2025
     */
    public void sendApplicationNotificationToUser(ApplicationConfirmationDTO confirmationDTO){
        try {

            /**
             * loading the user data from the db by the user email
             */
            Optional<User> user = userRepo.findUserByEmail(confirmationDTO.getUserEmail());

            if (user.isEmpty()){
                log.info("No user found with the email provided->>>{}", confirmationDTO.getUserEmail());
                throw new NotFoundException("No user record found with the email provide");
            }

            /**
             * setting email items
             */
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("eyidana001@gmail.com");
            helper.setTo(confirmationDTO.getEmail());

            /**
             * setting variables values to be passed to the template
             */
            Context context = new Context();
            context.setVariable("name", user.get().getName());
            context.setVariable("task", confirmationDTO.getTask()!=null?confirmationDTO.getTask():null);
            context.setVariable("category", confirmationDTO.getCategory()!=null?confirmationDTO.getCategory():null);
            context.setVariable("status", confirmationDTO.getStatus()!=null?confirmationDTO.getStatus():null);
            context.setVariable("startDate", confirmationDTO.getStartDate()!=null?confirmationDTO.getStartDate():null);
            context.setVariable("location", confirmationDTO.getLocation()!=null?confirmationDTO.getLocation():null);

            /**
             * determine which template to use base on status
             */
            if (confirmationDTO.getStatus().equalsIgnoreCase(AppConstants.APPROVED)){
                log.info("Sending application approval notification->>>{}", confirmationDTO.getStatus());
                helper.setSubject("Application Decision");
                String htmlContent = templateEngine.process("ApplicationApprovalTemplate", context);
                helper.setText(htmlContent, true);
            } else if (confirmationDTO.getStatus().equalsIgnoreCase(AppConstants.REJECTED)) {
                log.info("Sending application rejection notification->>>{}", confirmationDTO.getStatus());
                helper.setSubject("Application Decision");
                String htmlContent = templateEngine.process("ApplicationRejectionTemplate", context);
                helper.setText(htmlContent, true);
            }else if (confirmationDTO.getStatus().equalsIgnoreCase(AppConstants.CONFIRMATION)){
                log.info("Sending application confirmation notification->>>{}", confirmationDTO.getStatus());
                helper.setSubject("Application Confirmation");
                String htmlContent = templateEngine.process("ApplicationConfirmationTemplate", context);
                helper.setText(htmlContent, true);
           }else {
                log.error("Status provided does not exist:->>>{}", confirmationDTO.getStatus());
                throw new BadRequestException("Status does not exist");
            }

            /**
             * send notification here
             */
            mailSender.send(message);
            log.info("Application confirmation sent to:->>>{}", confirmationDTO.getEmail());

        } catch (Exception e) {
            log.info("Error message->>>{}", e.getMessage());
            throw new ServerException("Error occurred while trying to send notification");
        }
    }


    /**
     * @description this method is used to notify NGO on Task application by volunteer
     * @param confirmationDTO the payload to be sent to the notification template
     * @atuher Emmanuel Yidana
     * @createdAt 31 August 2025
     */
    public void sendApplicationNotificationToNGO(ApplicationConfirmationDTO confirmationDTO){
        try {

            /**
             * loading the user data from the db by the user email
             */
            NGO ngo = ngoRepo.findByEmail(confirmationDTO.getUserEmail());

            if (ngo==null){
                log.info("No NGO found with the email provided->>>{}", confirmationDTO.getUserEmail());
                throw new NotFoundException("No NGO record found with the email provide");
            }

            /**
             * setting email items
             */
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("eyidana001@gmail.com");
            helper.setTo(ngo.getEmail());

            /**
             * setting variables values to be passed to the template
             */
            Context context = new Context();
            context.setVariable("name", ngo.getOrganizationName());
            context.setVariable("task", confirmationDTO.getTask()!=null?confirmationDTO.getTask():null);
            context.setVariable("category", confirmationDTO.getCategory()!=null?confirmationDTO.getCategory():null);
            context.setVariable("status", confirmationDTO.getStatus()!=null?confirmationDTO.getStatus():null);
            context.setVariable("startDate", confirmationDTO.getStartDate()!=null?confirmationDTO.getStartDate():null);
            context.setVariable("reason", confirmationDTO.getReason()!=null?confirmationDTO.getReason():null);
            context.setVariable("applicant", confirmationDTO.getApplicant()!=null?confirmationDTO.getApplicant():null);


            helper.setSubject("Task Application Decision");
            String htmlContent = templateEngine.process("NGOApplicationNotificationTemplate", context);
            helper.setText(htmlContent, true);

            /**
             * send notification here
             */
            mailSender.send(message);
            log.info("Application notification sent to NGO:->>>{}", ngo.getEmail());

        } catch (Exception e) {
            log.info("Error message->>>{}", e.getMessage());
            throw new ServerException("Error occurred while trying to send notification");
        }
    }


    /**
     * @description this method is used to notify NGO either on
     * Account Rejection OR Account Approval
     * @param confirmationDTO the payload to be sent to the notification template
     * @atuher Emmanuel Yidana
     * @createdAt 31 August 2025
     */
    public void sendAccountDecisionNotificationToNGO(ApplicationConfirmationDTO confirmationDTO){
        try {

            /**
             * loading the user data from the db by the user email
             */
            NGO ngo = ngoRepo.findByEmail(confirmationDTO.getUserEmail());

            if (ngo==null){
                log.info("No NGO found with the email provided->>>{}", confirmationDTO.getUserEmail());
                throw new NotFoundException("No NGO record found with the email provide");
            }

            /**
             * setting email items
             */
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("eyidana001@gmail.com");
            helper.setTo(confirmationDTO.getEmail());

            /**
             * setting variables values to be passed to the template
             */
            Context context = new Context();
            context.setVariable("name", ngo.getOrganizationName());
            context.setVariable("email", ngo.getEmail());

            /**
             * determine which template to use base on type and status
             */
            if (confirmationDTO.getStatus().equalsIgnoreCase(AppConstants.APPROVED)){
                log.info("Sending NGO account approval notification->>>{}", confirmationDTO.getStatus());
                helper.setSubject("NGO Account Decision");
                String htmlContent = templateEngine.process("NGOApprovalTemplate", context);
                helper.setText(htmlContent, true);
            }else if(confirmationDTO.getStatus().equalsIgnoreCase(AppConstants.REJECTED)){
                log.info("Sending NGO account rejection notification->>>{}", confirmationDTO.getStatus());
                helper.setSubject("NGO Account Decision");
                String htmlContent = templateEngine.process("NGORejectionTemplate", context);
                helper.setText(htmlContent, true);
            }else {
                log.error("Status provided does not exist:->>>{}", confirmationDTO.getStatus());
                throw new BadRequestException("Status does not exist");
            }

            /**
             * send notification here
             */
            mailSender.send(message);
            log.info("NGO Account decision sent sent to:->>>{}", confirmationDTO.getEmail());

        } catch (Exception e) {
            log.info("Error message->>>{}", e.getMessage());
            throw new ServerException("Error occurred while trying to send notification");
        }
    }
}
