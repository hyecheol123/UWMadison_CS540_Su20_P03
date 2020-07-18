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
  private static HashMap<String, Double> unigramProb;
  private static HashMap<String, Double> bigramProb;
  private static HashMap<String, Double> trigramProb;
  // Will be used later to calculate unigram probability
  private static int lengthScript;
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
    lengthScript = script.length();
    
    // counting number of occurance for each unigram, bigram, and trigram models
    countNGrams(script);

    // Q2: Unigram Probability
    unigramProb = new HashMap<>();
    String output = ""; // temporary space to store output String
    transitionProbability(1, false);
    for(char x : alphabet) {
      output += String.format("%.4f ", unigramProb.get(String.valueOf(x)));
    }
    resultFileWriter.append("@unigram\n");
    resultFileWriter.append(output.trim().replace(" ", ",") + "\n");
    resultFileWriter.flush();

    // Q3: Bigram Probability
    bigramProb = new HashMap<>();
    output = ""; // temporary space to store output String
    transitionProbability(2, false);
    for(char x : alphabet) {
      for(char y : alphabet) {
        String key = String.valueOf(x) + String.valueOf(y);
        output += String.format("%.4f ", bigramProb.get(key));
      }
      output = output.trim().replace(" ", ",") + "\n";
    }
    resultFileWriter.append("@bigram\n");
    resultFileWriter.append(output);
    resultFileWriter.flush();

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

  /**
   * Helper method to calculate transition probability
   * 
   * @param n nGram model (only support 1, 2, 3)
   * @param laplaceSmoothing 1 if you want to use laplace smoothing, 0 otherwise
   */
  private static void transitionProbability(int n, boolean laplaceSmoothing) {
    double probability;

    if(n == 1) { // unigram
      for(String key : unigramCount.keySet()) {
        // compute P(x)
        probability = (double)unigramCount.get(key) / lengthScript;
        unigramProb.put(key, probability);
      }
    } else if(n == 2) { // bigram
      for(String key : bigramCount.keySet()) {
        // compute P(y|x)
        probability = (double)bigramCount.get(key) / unigramCount.get(key.substring(0, 1));
        bigramProb.put(key, probability);
      }
    } else if(n == 3) { // trigram
      for(String key : trigramCount.keySet()) {
        // compute P(z|xy)
        probability = (double)trigramCount.get(key) / bigramCount.get(key.substring(0, 2));
        trigramProb.put(key, probability);
      }
    } else {
      System.out.println("Invalid nGram");
    }
  }
}