import java.security.SecureRandom;

public class PasswordGenerator{
  private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+";

  public static void main(String[] args){
    int passwordLength = 16; //Change this according to your desired length
    String generatedPassword = generatePassword(passwordLength);
    System.out.println("Generated Password: " + generatedPassword);
  }

  public static String generatedPassword(int length){
    SecureRandom random = new SecureRandom();
    StringBuilder password = new StringBuilder(length);

    for (int i = 0; i < length; i++){
      int randomIndex = random.nextInt(CHARACTERS.length());
      char randomChar = CHARACTERS.charAt(randomIndex);
      password.append(randomChar);
    }

    return password.toString();
  }
