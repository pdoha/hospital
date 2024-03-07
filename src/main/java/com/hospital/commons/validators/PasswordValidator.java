package com.hospital.commons.validators;

//추가제거 쉽게 인터페이스로
public interface PasswordValidator {


    //세 가지 메서드를 조합해서 쓰려고 각각 만들었음



    /**
     * 비밀 번호에 알파벳 포함 여부
     * @param password
     * @param caseIncensitive
     *          false: 대문자 1개 이상, 소문자 1개 이상 포함
     *          true: 대소문자 구분없이 1개 이상 포함
     * @return
     */
    default boolean alphaCheck(String password, boolean caseIncensitive){
        if(caseIncensitive) {
            //대소문자 구분없이 체크 (정규표현식 - 0개이상의 문자 1개)
            String pattern = ".*[a-zA-Z]+.*";

            return password.matches(pattern);

        } else {
            //대문자 1개 소문자 1개 포함
            String pattern1 = ".*[a-z]+.*"; //소문자
            String pattern2 = ".*[A-Z]+.*"; //대문자

            return password.matches(pattern1) && password.matches(pattern2);
        }

    }

    /**
     * 비밀번호에 숫자 포함 여부
     * @param password
     * @return
     */

    default boolean numberCheck(String password){
        //앞뒤 문자 포함되고 d = Digital 중간에 숫자가 포함되어있는
        return password.matches(".*\\d+.*");

    }

    /**
     * 비밀번호에 특수문자 포함 여부
     * @param password
     * @return
     */

    default boolean specialCharsCheck(String password){
        //문자하나하나를 의미하는데  [ ] 안에는 이중에서 한개만 있으면됨
        String pattern = ".*[`~!@#$%^*&()-_+=]+.*";

        return password.matches(pattern);

    }
}
