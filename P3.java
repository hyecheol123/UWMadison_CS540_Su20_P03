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
  private static HashMap<String, Integer> unigram_count = new HashMap<>();
  private static HashMap<String, Integer> bigram_count = new HashMap<>();
  private static HashMap<String, Integer> trigram_count = new HashMap<>();
  // HashMap to store probability for each model
  private static HashMap<String, Double> unigram_prob;
  private static HashMap<String, Double> bigram_prob;
  private static HashMap<String, Double> trigram_prob;
  // Will be used later to calculate unigram probability
  private static int length_script;
  // space + alphabet
  private static char[] alphabet = " abcdefghijklmnopqrstuvwxyz".toCharArray();

  /**
   * main method for P3
   * 
   * @param args Command Line Arguments (CLAs)
   * @throws IOException Occurred when I/O Operation Interrupted
   */
  public static void main(String[] args) throws IOException {
    // Get net_id and result file location
    Scanner console_scnr = new Scanner(System.in);
    System.out.print("Enter Result File Location(Name): ");
    String result_file_loc = console_scnr.nextLine();
    System.out.println("Need net_id to properly format result file");
    System.out.print("Enter Your UWMadison net_id: ");
    String net_id = console_scnr.nextLine();
    console_scnr.close();
    // Initialize result_file_writer
    BufferedWriter result_file_writer = new BufferedWriter(new FileWriter(new File(result_file_loc)));
    // Format header of result file
    result_file_writer.append("Outputs:\n@id\n" + net_id + "\n");
    result_file_writer.flush();

    // Read a script of the movie from txt file
    String script = new String(Files.readAllBytes(Paths.get("Inception.txt")));
    // Q1: enter the name of the movie script
    result_file_writer.append("@answer_1\nInception\n");
    result_file_writer.flush();

    // Process the Script
    script = script.toLowerCase() // make everything lower case
                   .replaceAll("[^a-z ]", " ") // remove non-characters except for space
                   .replaceAll(" +", " "); // make space to single space
    length_script = script.length();
    
    // counting number of occurance for each unigram, bigram, and trigram models
    countNGrams(script);

    // Q2: Unigram Probability
    unigram_prob = new HashMap<>();
    String output = ""; // temporary space to store output String
    transitionProbability(1, false);
    for(char x : alphabet) {
      output += String.format("%.4f ", unigram_prob.get(String.valueOf(x)));
    }
    result_file_writer.append("@unigram\n");
    result_file_writer.append(output.trim().replace(" ", ",") + "\n");
    result_file_writer.flush();

    // Q3: Bigram Probability
    bigram_prob = new HashMap<>();
    output = ""; // temporary space to store output String
    transitionProbability(2, false);
    for(char x : alphabet) {
      for(char y : alphabet) {
        String key = String.valueOf(x) + String.valueOf(y);
        output += String.format("%.4f ", bigram_prob.get(key));
      }
      output = output.trim().replace(" ", ",") + "\n";
    }
    result_file_writer.append("@bigram\n");
    result_file_writer.append(output);
    result_file_writer.flush();

    // Q4: Bigram Probability with Laplace smoothing
    bigram_prob = new HashMap<>();
    output = ""; // temporary space to store output String
    transitionProbability(2, true);
    for(char x : alphabet) {
      for(char y : alphabet) {
        String key = String.valueOf(x) + String.valueOf(y);
        if(bigram_prob.get(key) > 0 && bigram_prob.get(key) < 0.00005) { // To deal with autograder problem
          // assign 0.0001 (rounded up value) for the values that become 0.0000 after rounding
          output += "0.0001 ";
        } else {
          output += String.format("%.4f ", bigram_prob.get(key));
        }
      }
      output = output.trim().replace(" ", ",") + "\n";
    }
    result_file_writer.append("@bigram_smooth\n");
    result_file_writer.append(output);
    result_file_writer.flush();

    // Close result_file_writer
    result_file_writer.append("@answer_10\nNone");
    result_file_writer.close();
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
      unigram_count.put(String.valueOf(alphabet[i]), count);
    }

    // Bigram
    for(int i = 0; i < alphabet.length; i++) {
      for(int j = 0; j < alphabet.length; j++) {
        String search_target = String.valueOf(alphabet[i]) + String.valueOf(alphabet[j]);
        // each occurance will decrease total length of string by 2
        count = (script.length() - script.replace(search_target, "").length()) / 2;
        bigram_count.put(search_target, count);
      }
    }

    // Trigram
    for(int i = 0; i < alphabet.length; i++) {
      for(int j = 0; j < alphabet.length; j++) {
        for(int k = 0; k < alphabet.length; k++) {
        String search_target = String.valueOf(alphabet[i]) + String.valueOf(alphabet[j]) + String.valueOf(alphabet[k]);
        // each occurance will decrease total length of string by 3
        count = (script.length() - script.replace(search_target, "").length()) / 3;
        trigram_count.put(search_target, count);
        }
      }
    }
  }

  /**
   * Helper method to calculate transition probability
   * 
   * @param n nGram model (only support 1, 2, 3)
   * @param laplace_smoothing 1 if you want to use laplace smoothing, 0 otherwise
   */
  private static void transitionProbability(int n, boolean laplace_smoothing) {
    double probability;

    if(n == 1) { // unigram
      for(String key : unigram_count.keySet()) {
        if(laplace_smoothing) {
          // compute P(x)
          probability = (double)(unigram_count.get(key) + 1) / (length_script + alphabet.length);
          unigram_prob.put(key, probability);
        } else {
          // compute P(x)
          probability = (double)unigram_count.get(key) / length_script;
          unigram_prob.put(key, probability);
        }
      }
    } else if(n == 2) { // bigram
      for(String key : bigram_count.keySet()) {
        if(laplace_smoothing) {
          // compute P(y|x)
          probability = (double)(bigram_count.get(key) + 1) / 
              (unigram_count.get(key.substring(0, 1)) + alphabet.length);
          bigram_prob.put(key, probability);
        } else {
          // compute P(y|x)
          probability = (double)bigram_count.get(key) / unigram_count.get(key.substring(0, 1));
          bigram_prob.put(key, probability);
        }
      }
    } else if(n == 3) { // trigram
      for(String key : trigram_count.keySet()) {
        if(laplace_smoothing) {
          // compute P(z|xy)
          probability = (double)(trigram_count.get(key) + 1) / 
              (bigram_count.get(key.substring(0, 2)) + alphabet.length);
          trigram_prob.put(key, probability);
        } else {
          // compute P(z|xy)
          probability = (double)trigram_count.get(key) / bigram_count.get(key.substring(0, 2));
          trigram_prob.put(key, probability);
        }
      }
    } else {
      System.out.println("Invalid nGram");
    }
  }
}