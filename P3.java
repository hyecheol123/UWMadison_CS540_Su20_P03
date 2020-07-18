///////////////////////////////// FILE HEADER /////////////////////////////////
//
// Title:           UWMadison_CS540_Su20_P03
// This File:       P3.java
// Files:           P3.java
// External Class:  None
//
// GitHub Repo:    https://github.com/hyecheol123/UWMadison_CS540_Su20_P03
//
// Author
// Name:            Hyecheol (Jerry) Jang
// Email:           hyecheol.jang@wisc.edu
// Lecturer's Name: Young Wu
// Course:          CS540 (LEC 002 / Epic), Summer 2020
//
///////////////////////////// OUTSIDE REFERENCE  //////////////////////////////
//
// List of Outside Reference
//   1.
//
////////////////////////////////// KNOWN BUGS /////////////////////////////////
//
// List of Bugs
//   1.
//
/////////////////////////////// 80 COLUMNS WIDE //////////////////////////////

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Main class for P3
 */
public class P3 {
  // HashMap to Store counts for each models
  private static HashMap<String, Integer> unigramCount = new HashMap<>();
  private static HashMap<String, Integer> bigramCount = new HashMap<>();
  private static HashMap<String, Integer> trigramCount = new HashMap<>();
  // HashMap to store probability for each model
  private static HashMap<String, Integer> unigramProb = new HashMap<>();
  private static HashMap<String, Integer> bigramProb = new HashMap<>();
  private static HashMap<String, Integer> trigramProb = new HashMap<>();
  // space + alphabet
  private static char[] alphabet = " abcdefghijklmnopqrstuvwxyz".toCharArray();

  /**
   * main method for P3
   * 
   * @param args Command Line Arguments (CLAs)
   * @throws IOException Occurred when I/O Operation Interrupted
   */
  public static void main(String[] args) throws IOException {
    // Get netID and result file location
    Scanner consoleScnr = new Scanner(System.in);
    System.out.print("Enter Result File Location(Name): ");
    String resultFileLoc = consoleScnr.nextLine();
    System.out.println("Need NetID to properly format result file");
    System.out.print("Enter Your UWMadison NetID: ");
    String netID = consoleScnr.nextLine();
    consoleScnr.close();
    // Initialize resultFileWriter
    BufferedWriter resultFileWriter = new BufferedWriter(new FileWriter(new File(resultFileLoc)));
    // Format header of result file
    resultFileWriter.append("Outputs:\n@id\n" + netID + "\n");
    resultFileWriter.flush();

    // Read a script of the movie from txt file
    String script = new String(Files.readAllBytes(Paths.get("Inception.txt")));
    // Q1: enter the name of the movie script
    resultFileWriter.append("@answer_1\nInception\n");
    resultFileWriter.flush();

    // Process the Script
    script = script.toLowerCase() // make everything lower case
                   .replaceAll("[^a-z ]", " ") // remove non-characters except for space
                   .replaceAll(" +", " "); // make space to single space
    
    // counting number of occurance for each unigram, bigram, and trigram models
    countNGrams(script);

    // Close resultFileWriter
    resultFileWriter.append("@answer_10\nNone");
    resultFileWriter.close();
  }

  /**
   * Helper method to count existence of string(character) for each unigram, bigram, and trigram model
   * 
   * @param script String of movie script
   */
  private static void countNGrams(String script) {
    int count = 0;

    // Unigram
    for(int i = 0; i < alphabet.length; i++) {
      count = script.length() - script.replace(String.valueOf(alphabet[i]), "").length();
      unigramCount.put(String.valueOf(alphabet[i]), count);
    }

    // Bigram
    for(int i = 0; i < alphabet.length; i++) {
      for(int j = 0; j < alphabet.length; j++) {
        String searchTarget = String.valueOf(alphabet[i]) + String.valueOf(alphabet[j]);
        // each occurance will decrease total length of string by 2
        count = (script.length() - script.replace(searchTarget, "").length()) / 2;
        bigramCount.put(searchTarget, count);
      }
    }

    // Trigram
    for(int i = 0; i < alphabet.length; i++) {
      for(int j = 0; j < alphabet.length; j++) {
        for(int k = 0; k < alphabet.length; k++) {
        String searchTarget = String.valueOf(alphabet[i]) + String.valueOf(alphabet[j]) + String.valueOf(alphabet[k]);
        // each occurance will decrease total length of string by 3
        count = (script.length() - script.replace(searchTarget, "").length()) / 3;
        trigramCount.put(searchTarget, count);
        }
      }
    }
  }
}