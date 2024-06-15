package com.juls.accesskeymanager.data.models;




public class Password {
    private String password1;
    private String password2;
    private String mainPass;

    public Password(String pass1, String pass2){
        if (pass1.equals(pass2)){
            this.mainPass = pass1;
        }
    }

    
    /** 
     * @return String
     */
    public String getMainPass(){
        return this.mainPass;
    }

    public String getPassword1() {
        return password1;
    }

    public void setPassword1(String password1) {
        this.password1 = password1;
    }

    public String getPassword2() {
        return password2;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
    }

    
    
}
