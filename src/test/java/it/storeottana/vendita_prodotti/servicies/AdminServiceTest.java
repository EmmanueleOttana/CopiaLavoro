package it.storeottana.vendita_prodotti.servicies;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class AdminServiceTest {

    @Test
    void fixTelephoneNumber() {
        String tel = "34755896620";
        String tel1 = "347   5589      6620";
        String tel2 = "+39 347 5589 6620";

        System.out.println(tel.substring(1));
        String[] s = tel.split(" ");
        String[] s1= tel1.split(" ");
        String[] s2= tel2.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String x : s1){
            sb.append(x.trim());
        }
        if (sb.toString().toCharArray()[0] == '+'){
            System.out.println(sb);
        }else System.out.println("+39"+sb);

    }
}