/*
 * Created by Sandesh Jain, Ryan Wood, and Sean Gruber on 2021.5.14
 * Copyright © 2021 Sandesh Jain, Ryan Wood, and Sean Gruber. All rights reserved.
 */
package edu.vt.TwoFactorAuth;

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

/*
 The @Named class annotation designates the bean object created by this class
 as a Contexts and Dependency Injection (CDI) managed bean. The object reference
 of a CDI-managed bean can be @Inject'ed in another CDI-Managed bean so that
 the other CDI-managed bean can access the methods and properties of this bean.

 Using the Expression Language (EL) in a JSF XHTML page, you can invoke a CDI-managed
 bean's method or set/get its property by using the logical name given with the 'value'
 parameter of the @Named annotation, e.g.,
    #{textMessageController.methodName() or property name}
 */
@Named(value = "textMessageController")

/*
 The @RequestScoped annotation indicates that the user’s interaction with
 this CDI-managed bean will be active only in a single HTTP request.
 */
@RequestScoped

/**
 * This class sends a Multimedia Messaging Service (MMS) Text Message to a cellular (mobile) phone.
 */
public class TextMessageController {

    /*
    ==================
    Constructor Method
    ==================
     */
    public TextMessageController() {
    }

    /*
    ===============================
    Instance Variables (Properties)
    ===============================
     */
    private String cellPhoneNumber;         // Cell phone number to which MMS Text Message to be sent.
    private String cellPhoneCarrierDomain;  // Cell phone carrier company's MMS gateway domain name.
    private String mmsTextMessage;          // MMS text message content.
    private String otp;
    private String rcvd_otp;


    TextMessageController(String otp) {
        this.otp = otp;
    }

    Properties emailServerProperties;   // java.util.Properties
    Session emailSession;               // javax.mail.Session
    MimeMessage mimeEmailMessage;       // javax.mail.internet.MimeMessage

    /*
    =========================
    Getter and Setter Methods
    =========================
     */
    public String getCellPhoneNumber() {
        return cellPhoneNumber;
    }

    public void setCellPhoneNumber(String enteredCellPhoneNumber) {
        /*
        The input mask we imposed in TextMessage.xhtml formats the cell phone number as
        (540) 123-4567 to make it visually easy for the user to enter the phone number.

        However, the cell phone number must be formatted only with numbers as 5401234567
        since it is required by the MMS gateway domain.

        Therefore, we need to remove the non-numeric characters inserted by the input mask.

        We remove all non-numeric characters from the entered cell phone number by using
        Regular Expression (RegEx). RegEx "[^0-9.]" means "if not a digit 0 to 9".
         */
        this.cellPhoneNumber = enteredCellPhoneNumber.replaceAll("[^0-9.]", "");
    }

    public String getCellPhoneCarrierDomain() {
        return cellPhoneCarrierDomain;
    }

    public void setCellPhoneCarrierDomain(String cellPhoneCarrierDomain) {
        this.cellPhoneCarrierDomain = cellPhoneCarrierDomain;
    }

    public String getMmsTextMessage() {
        return mmsTextMessage;
    }

    public void setMmsTextMessage(String mmsTextMessage) {
        this.mmsTextMessage = mmsTextMessage;
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
    ================
    Instance Methods
    ================
     */
    public void clearTextMessage() {

        cellPhoneNumber = "";
        cellPhoneCarrierDomain = null;
        mmsTextMessage = "";
    }

    /*
    ============================================================
    Create Email Session and Transport Email in Plain Text Format
    ============================================================
     */
    public void sendTextMessage(String send_otp) throws AddressException, MessagingException {

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
            mimeEmailMessage = new MimeMessage(emailSession);

            /*
            Specify the email address to send the email message containing the text message as

                    5401234567@CellPhoneCarrier's MMS gateway domain

            The designated cell phone number will be charged for the text messaging by its carrier.
            Here are the MMS gateway domain names for some of the cell phone carriers and examples:

                mms.att.net     for AT&T            5401234567@mms.att.net
                pm.sprint.com   for Sprint          5401234567@pm.sprint.com
                tmomail.net     for T-Mobile        5401234567@tmomail.net
                vzwpix.com      for Verizon         5401234567@vzwpix.com
                vmpix.com       for Virgin Mobile   5401234567@vmpix.com
             */
            mimeEmailMessage.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(cellPhoneNumber + "@" + cellPhoneCarrierDomain));

            /*
             Since some cell phones may not be able to process text messages in the HTML format,
             send the email message containing the text message in Plain Text format.
             */
            mimeEmailMessage.setContent(mmsTextMessage, "text/plain");

            // Create a transport object that implements the Simple Mail Transfer Protocol (SMTP)
            Transport transport = emailSession.getTransport("smtp");

            /*
            Connect to Gmail's SMTP server using the username and password provided.
            For the Gmail's SMTP server to accept the unsecure connection, the
            Cloud.Software.Email@gmail.com account's "Allow less secure apps" option is set to ON.
             */
            transport.connect("host", "user", "pass");

            // Send the email message containing the text message to the specified email address
            transport.sendMessage(mimeEmailMessage, mimeEmailMessage.getAllRecipients());

            // Close this service and terminate its connection
            transport.close();

            Methods.showMessage("Information", "Success!", "MMS Text Message is Sent!");
            clearTextMessage();

        } catch (AddressException ae) {
            Methods.showMessage("Fatal Error", "Email Address Exception Occurred!",
                    "See: " + ae.getMessage());

        } catch (MessagingException me) {
            Methods.showMessage("Fatal Error",
                    "Email Messaging Exception Occurred! Internet Connection Required!",
                    "See: " + me.getMessage());
        }


    }

    public boolean confirm() {
        return Objects.equals(this.otp, this.rcvd_otp);
    }



    public boolean timer_auth() {
        long startTime = System.currentTimeMillis();
        long elapsedTime = 0L;

        while (elapsedTime < 2 * 60 * 1000) {
            if (confirm()) {
                return true;
            }


            elapsedTime = (new Date()).getTime() - startTime;
        }

        return false;

    }

}
