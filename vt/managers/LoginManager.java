/*
 * Created by Team 7: Sean Gruber, Sandesh Jain, Ryan Wood
 */
package edu.vt.managers;

import edu.vt.TwoFactorAuth.EmailController;
import edu.vt.globals.Constants;
import edu.vt.globals.Password;
import edu.vt.EntityBeans.User;
import edu.vt.FacadeBeans.UserFacade;
import edu.vt.globals.Methods;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

@Named("loginManager")
@SessionScoped
public class LoginManager implements Serializable {
    /*
    ===============================
    Instance Variables (Properties)
    ===============================
     */
    private String username;
    private String password;
    private String twofa;
    private String rec_tfa;

    public void setRec_tfa(String rec_tfa) {
        this.rec_tfa = rec_tfa;
    };
    public String getRec_tfa() {
        return rec_tfa;
    };
    public String getTwofa() {
        return twofa;
    }

    public void setTwofa(String twofa) {
        this.twofa = twofa;
        //System.out.println(Constants.TWO_FACTOR_PASSWORD);
    }

    /*
        The @EJB annotation directs the EJB Container Manager to inject (store) the object reference of the
        UserFacade bean into the instance variable 'userFacade' after it is instantiated at runtime.
         */
    @EJB
    private UserFacade userFacade;

    /*
    =========================
    Getter and Setter Methods
    =========================
     */
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /*
    ================
    Instance Methods
    ================
    */

    /*
    *****************************************************
    Sign in the User if the Entered Username and Password
    are Valid and Redirect to Show the Profile Page
    *****************************************************
     */
    public String loginUser() {

        // Since we will redirect to show the Profile page, invoke preserveMessages()
        Methods.preserveMessages();

        String enteredUsername = username;

        // Obtain the object reference of the User object from the entered username
        User user = userFacade.findByUsername(enteredUsername);

        if (user == null) {
            Methods.showMessage("Fatal Error", "Unknown Username!",
                    "Entered username " + enteredUsername + " does not exist!");
            return "";

        } else {
            String actualUsername = user.getUsername();

            if (!actualUsername.equals(enteredUsername)) {
                Methods.showMessage("Fatal Error", "Invalid Username!",
                        "Entered Username is Unknown!");
                return "";
            }

            /*
            Call the getter method to obtain the user's coded password stored in the database.
            The coded password contains the the following parts:
                "algorithmName":"PBKDF2_ITERATIONS":"hashSize":"salt":"hash"
             */
            String codedPassword = user.getPassword();

            // Call the getter method to get the password entered by the user
            String enteredPassword = getPassword();

            /*
            Obtain the user's password String containing the following parts from the database
                  "algorithmName":"PBKDF2_ITERATIONS":"hashSize":"salt":"hash"
            Extract the actual password from the parts and compare it with the password String
            entered by the user by using Key Stretching to prevent brute-force attacks.
             */
            try {
                if (!Password.verifyPassword(enteredPassword, codedPassword)) {
                    Methods.showMessage("Fatal Error", "Invalid Password!",
                            "Please Enter a Valid Password!");
                    return "";
                }

                else {
                    Random random = new Random();

                    String tfa_otp = String.format("%04d", random.nextInt(10000));
                    setTwofa(tfa_otp);
                    String emailTo = user.getEmail();
                    String emailBody = "The two factor authentication pin for your account long is: " + tfa_otp;
                    EmailController emailController = new EmailController(tfa_otp, emailTo, emailBody);
                    emailController.sendEmail();
//
//                    if(!timer_auth()){
//                        Methods.showMessage("Fatal Error", "Two Factor Authentication Not Valid!",
//                                "Please Try Again!");
//                        return "";
//                    }
                }

            } catch (Password.CannotPerformOperationException | Password.InvalidHashException ex) {
                Methods.showMessage("Fatal Error",
                        "Password Manager was unable to perform its operation!",
                        "See: " + ex.getMessage());
                return "";
            } catch (MessagingException e) {
                e.printStackTrace();
            }

            // Verification Successful: Entered password = User's actual password

            // Initialize the session map with user properties of interest in the method below
            initializeSessionMap(user);

            // Redirect to show the Profile page     "/userAccount/Profile?faces-redirect=true"
            return "";
        }
    }
    public String confirm(){
        Methods.preserveMessages();
        System.out.println(this.rec_tfa);
        System.out.println(this.twofa);
        if(Objects.equals(this.twofa, this.rec_tfa)){
            return "/userAccount/Profile?faces-redirect=true";
        }
        else {

            Methods.showMessage("Fatal Error", "Two Factor Authentication Not Valid!",
                    "Please Try Again!");
            return "/SignIn?faces-redirect=true";
        }
    };

    public boolean timer_auth() {
        long startTime = System.currentTimeMillis();
        long elapsedTime = 0L;

        while (elapsedTime < 2 * 120 * 1000) {
            if (confirm() == "") {
                return true;
            }

            elapsedTime = (new Date()).getTime() - startTime;
        }

        return false;
    }
    /*
    ******************************************************************
    Initialize the Session Map to Hold Session Attributes of Interests
    ******************************************************************
     */
    public void initializeSessionMap(User user) {

        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();

        // Store the object reference of the signed-in user
        sessionMap.put("user", user);

        // Store the First Name of the signed-in user
        sessionMap.put("first_name", user.getFirstName());

        // Store the Last Name of the signed-in user
        sessionMap.put("last_name", user.getLastName());

        // Store the Username of the signed-in user
        sessionMap.put("username", username);

        // Store signed-in user's Primary Key in the database
        sessionMap.put("user_id", user.getId());
    }




}