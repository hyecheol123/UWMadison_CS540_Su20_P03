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
import java.util.Random;
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

    // trigram transition probability table
    trigram_prob = new HashMap<>();
    transitionProbability(3, true);

    // Generate new Sentence of length 1000 starting with a to z
    String[] generated_string = new String[26];
    for(int i = 1; i < alphabet.length; i++) {
      generated_string[i - 1] = generateString(1000, alphabet[i]);
    }
    // Q5: 26 sentences generated by the trigram and bigram models
    result_file_writer.append("@sentences\n");
    for(String sentence : generated_string) {
      result_file_writer.append(sentence + "\n");
    }
    // Q6: one interesting sentence that at least contains English words
    result_file_writer.append("@answer_6\n");
    result_file_writer.append(generated_string[7] + "\n");
    result_file_writer.flush();

    // Likelihood Calculation for Randomly Generated(Given) non-English String
    HashMap<String, Double> likelihood_prob = new HashMap<>(); // HashMap to store likelihood probability
    // Read a script of the movie from txt file
    String rnd_script = new String(Files.readAllBytes(Paths.get("random_script.txt")));
    // Process the Script
    rnd_script = rnd_script.toLowerCase() // make everything lower case
                   .replaceAll("[^a-z ]", " ") // remove non-characters except for space
                   .replaceAll(" +", " "); // make space to single space
    // calculate likelihood
    likelihoodProbCalculate(rnd_script, likelihood_prob);
    // Q7: Enter likelihood probabilities of the Naive Bayes estimator for my script (random_script.txt)
    output = ""; // temporary space to store output String
    for(char x : alphabet) {
      output += String.format("%.4f ", likelihood_prob.get(String.valueOf(x)));
    }
    result_file_writer.append("@likelihood\n");
    result_file_writer.append(output.trim().replace(" ", ",") + "\n");
    result_file_writer.flush();

    // Q8: Enter posterior probabilities of the Naive Bayes estimator for my script (random_script.txt)
    // Pr{D = Doc1 | "a"} = Pr{"a", D = Doc1} / (Pr{"a", D = Doc1} + Pr{"a", D = Doc0})
    HashMap<Character, Double> posterior_rnd = new HashMap<>(); // place to store calculated result for random document
    output = ""; // temporary space to store output String
    for(char x : alphabet) { // calculate posterior probability for all characters
      double doc_t_prob = likelihood_prob.get(String.valueOf(x)) * 0.5;
      double doc_other_prob = unigram_prob.get(String.valueOf(x)) * 0.5;
      posterior_rnd.put(x, doc_t_prob / (doc_t_prob + doc_other_prob));
      // generating output
      output += String.format("%.4f ", posterior_rnd.get(x));
    }
    result_file_writer.append("@posterior\n");
    result_file_writer.append(output.trim().replace(" ", ",") + "\n");
    result_file_writer.flush();

    // Q9: Use the Naive Bayes model to predict which document the 26 sentences
    output = ""; // temporary space to store output String
    for(String sentence : generated_string) {
      // Place to store current likelihood
      double rnd_likelihood = 0;
      double english_likelihood = 0;
      // Make sentence to char array to access all characters easily
      char[] char_array = sentence.toCharArray();

      // Calculate likelihood
      // log{Pr(D = doc_x | first letter)} + log{Pr(D = doc_x | first letter)} + ...
      for(char current_char : char_array) {
        rnd_likelihood += Math.log(posterior_rnd.get(current_char));
        english_likelihood += Math.log(1 - posterior_rnd.get(current_char));
      }

      // Compare the likelihood
      if(english_likelihood > rnd_likelihood) {
        output += "0 ";
      } else {
        output += "1 ";
      }
    }
    result_file_writer.append("@predictions\n");
    result_file_writer.append(output.trim().replace(" ", ",") + "\n");
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

  /**
   * Helper method to generate String start with specific character and have specific length using trigram model
   * 
   * @param length length of generating sentence
   * @param start_character the string with start with this character
   * @return generated string
   */
  private static String generateString(int length, char start_character) {
    String generated_string = String.valueOf(start_character);
    Random rnd = new Random(); // To generate random probability between 0 and 1
    double current_prob; // current probability that generated randomly

    // Use Bigram for the second place
    current_prob = rnd.nextDouble(); // Retrieve random probability
    for(int j = 0; j < alphabet.length; j++) { // retrieve probability table and figure out the new character
      String search_target = generated_string.substring(0, 1) + String.valueOf(alphabet[j]);
      current_prob -= bigram_prob.get(search_target);
      if(current_prob <= 0) { // when the probability is between prob[i] and prob[i+1], we got the character
        generated_string += String.valueOf(alphabet[j]);
        break;
      }
    }

    // Use Trigram for the remaining places
    // (If there is no such occurance of previously observed two character in training set, use bigram)
    for(int i = 2; i < length; i++) {
      // check for previous occurance of condition
      if(bigram_count.get(generated_string.substring(i - 2, i)) != 0) { // Trigram
        current_prob = rnd.nextDouble(); // retrieve random probability
        for(int j = 0; j < alphabet.length; j++) { // retrieve probability table and figure out the new character
          String search_target = generated_string.substring(i - 2, i) + String.valueOf(alphabet[j]);
          current_prob -= trigram_prob.get(search_target);
          if(current_prob <= 0) { // when the probability is between prob[i] and prob[i+1], we got the character
            generated_string += String.valueOf(alphabet[j]);
            break;
          }
        }
        
      } else { // Bigram
        current_prob = rnd.nextDouble(); // retrieve random probability
        for(int j = 0; j < alphabet.length; j++) { // retrieve probability table and figure out the new character
          String search_target = generated_string.substring(i - 1, i) + String.valueOf(alphabet[j]);
          current_prob -= bigram_prob.get(search_target);
          if(current_prob <= 0) { // when the probability is between prob[i] and prob[i+1], we got the character
            generated_string += String.valueOf(alphabet[j]);
            break;
          }
        }
      }
    }

    return generated_string;
  }

  /**
   * Helper to calculate likelihood
   * 
   * @param script script to calculate likelihood for each character
   * @param prob HashMap to store likelihood probability
   */
  private static void likelihoodProbCalculate(String script, HashMap<String, Double> prob) {
    int total_characters = script.length();

    for(char target : alphabet) {
      prob.put(String.valueOf(target),
          (double)(script.length() - script.replace(String.valueOf(target), "").length()) / total_characters);
    }
  }
}