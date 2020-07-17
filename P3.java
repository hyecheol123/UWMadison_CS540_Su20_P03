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
import java.util.Scanner;

/**
 * Main class for P3
 */
public class P3 {

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


  }
}