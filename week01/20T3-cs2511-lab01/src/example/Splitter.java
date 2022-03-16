package example;

import java.util.Scanner;

public class Splitter {

    public static void main(String[] args){
        System.out.print("Enter a sentence specified by spaces only: ");
        Scanner scan = new Scanner(System.in);
        String line = scan.nextLine();
        String[] array = line.split(" ");
        for(String word : array){
            System.out.println(word);
        }
    }
}
