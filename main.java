/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package terrier;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.terrier.indexing.Collection;
import org.terrier.indexing.SimpleFileCollection;
import org.terrier.matching.ResultSet;
import org.terrier.querying.Manager;
import org.terrier.querying.SearchRequest;
import org.terrier.structures.Index;
import org.terrier.structures.indexing.Indexer;
import org.terrier.structures.indexing.classical.BasicIndexer;
import org.terrier.utility.ApplicationSetup;

/**
 *
 * @author Nikos
 */
public class main {

    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        BufferedReader consoleInput, fileInput = null;
        String queryPath = null;   // string for query-text.trec path

        /* code to read file path from console */
        consoleInput = new BufferedReader(new InputStreamReader(System.in));    // creating the object for the job
        try {
            System.out.print("Give the file path: ");
            queryPath = consoleInput.readLine();    // read the whole line

            consoleInput.close();   // close the object
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        /* ***** */

 /* code to read the file with querys */
        try {
            fileInput = new BufferedReader(new FileReader(queryPath));  // creating the object for the job
            //fileInput = new BufferedReader(new FileReader("E:\\terrier-core-4.2\\share\\vaswani_npl\\query-text.trec"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        String line;    // temp string to save each file line
        ArrayList<String> queryList = new ArrayList<String>();  // list to save all querys from the file
        try {
            while ((line = fileInput.readLine()) != null) { // read the file line by line
                if (!line.startsWith("<")) { // add into list only the querys and avoid <top>, <num>, etc ...
                    queryList.add(line);    // add the line to list
                }
            }

            fileInput.close();  // close the object 
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileInput != null) {    // check if something went wrong
                    fileInput.close();
                }

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        /* ***** */

        /* print all querys */
        for (String s : queryList) {
            System.out.println(s);
        }
        /* ***** */

        /* indexing */
        // Directory containing files to index
        String aDirectoryToIndex = "C:\\terrier-core-4.2\\share\\vaswani_npl\\corpus";

        /*
        // Configure Terrier
        ApplicationSetup.setProperty("indexer.meta.forward.keys", "filename");
        ApplicationSetup.setProperty("indexer.meta.forward.keylens", "200");
         */
        Indexer indexer = new BasicIndexer("C:\\terrier-core-4.2\\var\\index", "data");
        Collection coll = new SimpleFileCollection(Arrays.asList(aDirectoryToIndex), true);
        indexer.index(new Collection[]{coll});

        Index index = Index.createIndex("C:\\terrier-core-4.2\\var\\index", "data");

        /*
        // Enable the decorate enhancement
        ApplicationSetup.setProperty("querying.postfilters.order", "org.terrier.querying.SimpleDecorate");
        ApplicationSetup.setProperty("querying.postfilters.controls", "decorate:org.terrier.querying.SimpleDecorate");
         */
        
        // Create a new manager run queries
        Manager queryingManager = new Manager(index);

        for (String s : queryList) {
            // Create a search request
            SearchRequest srq = queryingManager.newSearchRequestFromQuery(s);

            // Specify the model to use when searching
            srq.addMatchingModel("Matching", "BM25");

            // Turn on decoration for this search request
            srq.setControl("decorate", "on");

            // Run the search
            queryingManager.runSearchRequest(srq);

            // Get the result set
            ResultSet results = srq.getResultSet();

            // Print the results
            System.out.println(results.getExactResultSize() + " documents were scored");
            System.out.println("The top " + results.getResultSize() + " of those documents were returned");
            System.out.println("Document Ranking");
            for (int i = 0; i < results.getResultSize(); i++) {
                int docid = results.getDocids()[i];
                double score = results.getScores()[i];
                System.out.println("   Rank " + i + ": " + docid + " " + results.getMetaItem("filename", docid) + " " + score);
            }
        }
    }
}
