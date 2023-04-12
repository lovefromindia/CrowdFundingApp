package org.crowdfund.utils;

public class Validator {

    public static boolean checkPasswordStrength(String password)
    {

        boolean lowerCase = false;

        boolean upperCase = false;

        boolean digit = false;

        boolean spCharacter = false;

        boolean len = password.length()>=8;

        for(int i = 0;i < password.length();i++)
        {

            lowerCase |= (password.charAt(i)>='a' && password.charAt(i)<='z');

            upperCase |= (password.charAt(i)>='A' && password.charAt(i)<='Z');

            digit |= (password.charAt(i)>='0' && password.charAt(i)<='9');

            spCharacter |= checkSpChar(password.charAt(i));

        }

        return lowerCase&&upperCase&&digit&&spCharacter&&len;

    }

    private static boolean checkSpChar(char c)
    {

        String spCharacterAllowed = "@#.^$*";

        boolean flag = false;

        for(int i = 0;i < spCharacterAllowed.length();i++)
        {

            flag |= (c == spCharacterAllowed.charAt(i));

        }

        return flag;

    }

    public static boolean checkName(String name)
    {

        return name.matches("^[a-zA-Z]{3,20}$");

    }

    public static boolean checkUserId(String id)
    {
        return id.matches("^[0-9]{10}$");
    }

    public static<T> boolean nullCheck(T value)
    {
        return value==null;
    }
}
