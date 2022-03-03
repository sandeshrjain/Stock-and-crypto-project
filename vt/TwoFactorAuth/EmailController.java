/*
 * Created by Sandesh Jain, Ryan Wood, and Sean Gruber on 2021.5.14
 * Copyright © 2021 Sandesh Jain, Ryan Wood, and Sean Gruber. All rights reserved.
 */
package edu.vt.TwoFactorAuth;

import edu.vt.globals.Constants;
import edu.vt.globals.Methods;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.*;

/*
 The @Named class annotation designates the bean object created by this class
 as a Contexts and Dependency Injection (CDI) managed bean. The object reference
 of a CDI-managed bean can be @Inject'ed in another CDI-Managed bean so that
 the other CDI-managed bean can access the methods and properties of this bean.

 Using the Expression Language (EL) in a JSF XHTML page, you can invoke a CDI-managed
 bean's method or set/get its property by using the logical name given with the 'value'
 parameter of the @Named annotation, e.g., #{emailController.methodName() or property name}
 */
@Named(value = "emailController")
/*
 The @RequestScoped annotation indicates that the user’s interaction with
 this CDI-managed bean will be active only in a single HTTP request.
 */
@RequestScoped

public class EmailController {

    /*
    ==================
    Constructor Method
    ==================
     */
    public EmailController() {
    }

    /*
    ===============================
    Instance Variables (Properties)
    ===============================
     */

    private String emailTo;             // Contains only one email address to send email to
    private String emailSubject;        // Subject line of the email message
    private String emailBody;           // Email content created in HTML format with PrimeFaces Editor
    private String otp;
    private String rcvd_otp;

    Properties emailServerProperties;   // java.util.Properties
    Session emailSession;               // javax.mail.Session
    MimeMessage htmlEmailMessage;       // javax.mail.internet.MimeMessage

    public EmailController(String otp, String emailTo, String emailBody) {
        this.emailBody = emailBody;
        this.emailTo = emailTo;
        this.otp = otp;


    }

    /*
    ************************************************************************************************
    The import javax.inject.Inject; brings in the javax.inject package into our project.
    "This package specifies a means for obtaining objects in such a way as to maximize
    reusability, testability and maintainability compared to traditional approaches such as
    constructors, factories, and service locators (e.g., JNDI). This process, known as
    dependency injection, is beneficial to most nontrivial applications." [Oracle]

    The @Inject annotation of the instance variable "private EditorController editorController;"
    directs the CDI Container Manager to store the object reference of the EditorController class
    bean object, after it is instantiated at runtime, into the instance variable "editorController".

    With this injection, the instance variables and instance methods of the EditorController
    class can be accessed in this CDI-managed bean.
    ************************************************************************************************
     */

    /*
    =========================
    Getter and Setter Methods
    =========================
     */

    public String getEmailTo() {
        return emailTo;
    }

    public void setEmailTo(String emailTo) {
        this.emailTo = emailTo;
    }

    public String getEmailSubject() {
        return emailSubject;
    }

    public void setEmailSubject(String emailSubject) {
        this.emailSubject = emailSubject;
    }

    public String getEmailBody() {
        return emailBody;
    }

    public void setEmailBody(String emailBody) {
        this.emailBody = emailBody;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getRcvd_otp() {
        return rcvd_otp;
    }

    public void setRcvd_otp(String rcvd_otp) {
        this.rcvd_otp = rcvd_otp;
    }

    /*
        ======================================================
        Create Email Sesion and Transport Email in HTML Format
        ======================================================
         */
    public void sendEmail() throws AddressException, MessagingException {

        // Obtain the email message content from the editorController object
        emailBody = "Your two factor authentication password is: " + otp;

        // Set Email Server Properties
        emailServerProperties = System.getProperties();
        emailServerProperties.put("mail.smtp.port", "587");
        emailServerProperties.put("mail.smtp.auth", "true");
        emailServerProperties.put("mail.smtp.starttls.enable", "true");

        try {
            // Create an email session using the email server properties set above
            emailSession = Session.getDefaultInstance(emailServerProperties, null);

            /*
            Create a Multi-purpose Internet Mail Extensions (MIME) style email
            message from the MimeMessage class under the email session created.
             */
            htmlEmailMessage = new MimeMessage(emailSession);

            // Set the email TO field to emailTo, which can contain only one email address
            htmlEmailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(emailTo));


            // Set the email subject line
            htmlEmailMessage.setSubject(emailSubject);

            // Set the email body to the HTML type text
            htmlEmailMessage.setContent(emailBody, "text/html");

            // Create a transport object that implements the Simple Mail Transfer Protocol (SMTP)
            Transport transport = emailSession.getTransport("smtp");

            /*
            Connect to Gmail's SMTP server using the username and password provided.
            For the Gmail's SMTP server to accept the unsecure connection, the
            Cloud.Software.Email@gmail.com account's "Allow less secure apps" option is set to ON.
             */
            transport.connect("host", "user", "pass");

            // Send the htmlEmailMessage created to the specified list of addresses (recipients)
            transport.sendMessage(htmlEmailMessage, htmlEmailMessage.getAllRecipients());

            // Close this service and terminate its connection
            transport.close();

            Methods.showMessage("Information", "Success!", "Email Message is Sent!");

        } catch (AddressException ae) {
            Methods.showMessage("Fatal Error", "Email Address Exception Occurred!",
                    "See: " + ae.getMessage());

        } catch (MessagingException me) {
            Methods.showMessage("Fatal Error",
                    "Email Messaging Exception Occurred! Internet Connection Required!",
                    "See: " + me.getMessage());
        }
    }
}

//    public boolean confirm(){
//
//        return Objects.equals(this.otp, Constants.TWO_FACTOR_PASSWORD);
//    };

//public boolean timer_auth() {
//    long startTime = System.currentTimeMillis();
//    long elapsedTime = 0L;
//
//    while (elapsedTime < 2 * 120 * 1000) {
//        if(confirm()) {
//            return true;
//        };
//
//        elapsedTime = (new Date()).getTime() - startTime;
//    }
//
//    return false;
//
//
//
//}
////Throw your exception
//
//
//}
