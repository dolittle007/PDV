package PDVGUI.fileimport;

import PDVGUI.DB.SQLiteConnection;
import PDVGUI.gui.PDVMainClass;
import com.compomics.util.experiment.biology.Peptide;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import com.compomics.util.experiment.identification.matches.SpectrumMatch;
import com.compomics.util.experiment.identification.spectrum_assumptions.PeptideAssumption;
import com.compomics.util.experiment.massspectrometry.Charge;
import com.compomics.util.experiment.massspectrometry.Spectrum;
import com.compomics.util.experiment.massspectrometry.SpectrumFactory;
import com.compomics.util.gui.waiting.waitinghandlers.ProgressDialogX;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.List;

/**
 * Import maxQUANT result file
 * Created by Ken on 10/23/2017.
 */
public class MaxQuantFileImport {

    /**
     * All apl files list
     */
    private ArrayList<File> allAPLFiles = new ArrayList<>();
    /**
     * All silic apl files list
     */
    private ArrayList<File> allSilAPLFiles = new ArrayList<>();
    /**
     * MsMsScan file
     */
    private File scansDetailFile;
    /**
     * Parameters File
     */
    private File parametersFile;
    /**
     * MsMs file
     */
    private File msResultFile;
    /**
     * modificationSpecificPeptides
     */
    private File modificationSpecificPeptidesFile;
    /**
     * Combined folder path
     */
    private File maxQuantDirectory;
    /**
     * File to title to RT map
     */
    private HashMap<String, HashMap<String, String>> fileToTitleToRTMap = new HashMap<>();
    /**
     * File to index map
     */
    private HashMap<String, Integer> fileToCountMap = new HashMap<>();
    /**
     * All spectrum matches
     */
    private HashMap<Integer, SpectrumMatch> allSpectrumMatches = new HashMap<>();
    /**
     * Spectrum title to rank
     */
    private HashMap<String, Integer> spectrumTitleToRank = new HashMap<>();
    /**
     * All modification files
     */
    private ArrayList<File> modificationFiles = new ArrayList<>();
    /**
     * All varied modifications
     */
    private ArrayList<String> variedModifications = new ArrayList<>();
    /**
     * Modification map
     */
    private HashMap<String, ArrayList<String>> fileNameToPositionList = new HashMap<>();
    /**
     * File name to sequence list
     */
    private HashMap<String, ArrayList<String>> fileNameToSeqList = new HashMap<>();
    /**
     * msIDSToModificationFileToEachSite
     */
    private HashMap<ArrayList<String>, HashMap<String, ArrayList<String>>> msIDSToModificationFileToEachSite = new HashMap<>();
    /**
     * All modification
     */
    private ArrayList<String> allModifications = new ArrayList<>();
    /**
     * Parent class
     */
    private PDVMainClass pdvMainClass;
    /**
     * Database connection
     */
    private SQLiteConnection sqLiteConnection;
    /**
     * Progress dialog
     */
    private ProgressDialogX progressDialog;
    /**
     * raw file column index
     */
    private int rawFileIndex = 0;
    /**
     * scan number column index
     */
    private int scanNumIndex = 0;
    /**
     * sequence column index
     */
    private int sequenceIndex = 0;
    /**
     * modification column index
     */
    private int modificationIndex = 0;
    /**
     * modification sequence column index
     */
    private int modificationSequenceIndex = 0;
    /**
     * mass error column index
     */
    private int massErrorIndex = 0;
    /**
     * score column index
     */
    private int scoreIndex = 0;
    /**
     * charge column index
     */
    private int chargeIndex = 0;
    /**
     * scan index column index
     */
    private int scanIndexIndex =0;
    /**
     * Fixed modification map
     */
    private HashMap<String, String> fixedModificationMap = new HashMap<>();

    /**
     * Constructor
     * @param pdvMainClass Parent class
     * @param maxQuantDirectory File folder path
     * @param progressDialog Progress dialog
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public MaxQuantFileImport(PDVMainClass pdvMainClass, File maxQuantDirectory, ProgressDialogX progressDialog) throws SQLException, ClassNotFoundException {

        this.pdvMainClass = pdvMainClass;
        this.maxQuantDirectory = maxQuantDirectory;
        this.progressDialog = progressDialog;

        String databasePath = maxQuantDirectory.getAbsolutePath()+"/combined.db";

        File dbFile = new File(databasePath);
        File dbJournalFile = new File(databasePath + "-journal");
        if (dbFile.isFile() && dbFile.exists()) {
            dbFile.delete();
        }
        if (dbJournalFile.isFile() && dbJournalFile.exists()) {
            dbJournalFile.delete();
        }

        sqLiteConnection = new SQLiteConnection(databasePath);

        sqLiteConnection.setScoreNum(1);

        getAllFiles();
    }

    /**
     * Get all necessary files
     */
    private void getAllFiles(){

        Boolean isCombine = false;

        if(maxQuantDirectory != null && maxQuantDirectory.isDirectory() && maxQuantDirectory.listFiles() != null){

            for(File eachFileInMax : maxQuantDirectory.listFiles()){
                if(eachFileInMax.isDirectory() && eachFileInMax.getName().equals("andromeda")){

                    isCombine = true;

                    for(File eachFileInAnd : eachFileInMax.listFiles()){
                        if(eachFileInAnd.getName().contains(".apl") && eachFileInAnd.getName().contains(".iso")){
                            allAPLFiles.add(eachFileInAnd);
                        }else if (eachFileInAnd.getName().contains(".apl") && !eachFileInAnd.getName().contains(".iso")){
                            allSilAPLFiles.add(eachFileInAnd);
                        }
                    }
                } else if(eachFileInMax.isDirectory() && eachFileInMax.getName().equals("txt")){
                    for(File eachFileInTxt : eachFileInMax.listFiles()){
                        if(eachFileInTxt.getName().equals("msms.txt")){
                            msResultFile = eachFileInTxt;
                        } else if(eachFileInTxt.getName().equals("msmsScans.txt")){
                            scansDetailFile = eachFileInTxt;
                        } else if(eachFileInTxt.getName().equals("parameters.txt")){
                            parametersFile = eachFileInTxt;
                        } else if (eachFileInTxt.getName().contains("Sites")){
                            modificationFiles.add(eachFileInTxt);

                            variedModifications.add(eachFileInTxt.getName().split("Sites")[0].replace("-_", "->"));
                        } else if (eachFileInTxt.getName().equals("modificationSpecificPeptides.txt")){
                            modificationSpecificPeptidesFile = eachFileInTxt;
                        }
                    }
                }
            }
        } else {
            progressDialog.setRunFinished();

            JOptionPane.showMessageDialog(
                    pdvMainClass, "Failed to parse MaxQuant file.\n Please check your file.",
                    "File Error", JOptionPane.ERROR_MESSAGE);
        }

        if (!isCombine){
            progressDialog.setRunFinished();

            JOptionPane.showMessageDialog(
                    pdvMainClass, "Failed to parse MaxQuant file.\n Please check your file.",
                    "File Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Get retention time and file scanNum
     * @param existMGF Exist mgf files
     * @throws IOException
     */
    public void getAllSpectrumRT(Boolean existMGF) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(scansDetailFile));

        String line;
        String[] values;
        String spectrumTitle;
        String rawFileName = "a";

        HashMap<String, String> titleToRTMap;

        int lineCount = 0;
        int rawFileIndex = 0;
        int scanNumIndex = 0;
        int rtIndex = 0;

        if(existMGF){
            while ((line = bufferedReader.readLine()) != null) {
                values = line.split("\t");

                if(lineCount == 0){
                    for(int index = 0;index<values.length;index++){
                        switch (values[index]) {
                            case "Raw file":
                                rawFileIndex = index;
                                break;
                        }
                    }
                }else {
                    if(!rawFileName.equals(values[rawFileIndex])){
                        rawFileName = values[rawFileIndex];
                        fileToCountMap.put(rawFileName, lineCount-1);
                    }
                }
                lineCount ++;
            }bufferedReader.close();
        }else {
            while ((line = bufferedReader.readLine()) != null) {
                values = line.split("\t");

                if(lineCount == 0){
                    for(int index = 0;index<values.length;index++){
                        switch (values[index]) {
                            case "Raw file":
                                rawFileIndex = index;
                                break;
                            case "Scan number":
                                scanNumIndex = index;
                                break;
                            case "Retention time":
                                rtIndex = index;
                                break;
                        }
                    }
                }else {

                    if(!rawFileName.equals(values[rawFileIndex])){
                        rawFileName = values[rawFileIndex];
                        fileToCountMap.put(rawFileName, lineCount-1);
                    }

                    spectrumTitle = "RawFile: "+values[rawFileIndex]+" Index: "+values[scanNumIndex];
                    if(fileToTitleToRTMap.containsKey(values[rawFileIndex])){
                        fileToTitleToRTMap.get(values[rawFileIndex]).put(spectrumTitle, values[rtIndex]);
                    }else {
                        titleToRTMap = new HashMap<>();
                        titleToRTMap.put(spectrumTitle, values[rtIndex]);
                        fileToTitleToRTMap.put(values[rawFileIndex], titleToRTMap);
                    }
                }
                lineCount ++;
            }bufferedReader.close();
        }
    }

    /**
     * Get all modification
     */
    public void getAllModificationSites(){

        BufferedReader bufferedReader;
        ArrayList<String> eachFile;
        ArrayList<String> eachFileSeq;

        for (File eachModificationFile : modificationFiles){
            eachFile = new ArrayList<>();
            eachFileSeq = new ArrayList<>();

            try {
                bufferedReader = new BufferedReader(new FileReader(eachModificationFile));

                String line;
                String[] lineElements;
                Integer count = 0;
                Integer positionIndex = 0;
                Integer modSeqIndex = 0;
                String modSeq;
                StringBuilder stringBuilder;

                while ((line = bufferedReader.readLine()) != null){

                    lineElements = line.split("\t");
                    if (count == 0){
                        for (Integer index = 0; index < lineElements.length; index ++){
                            if (lineElements[index].equals("Position in peptide")){
                                positionIndex = index;
                            } else if (lineElements[index].contains("Score diffs")){
                                modSeqIndex = index;
                            }
                        }
                    } else {
                        stringBuilder = new StringBuilder();

                        eachFile.add(lineElements[positionIndex]);
                        modSeq = lineElements[modSeqIndex];
                        String[] seqArray = modSeq.split("\\(");
                        for (Integer index = 0; index < seqArray.length; index ++){
                            if (index == 0){
                                stringBuilder.append(seqArray[0]);
                            } else {
                                if (seqArray[index].split("\\)").length > 1){
                                    stringBuilder.append(seqArray[index].split("\\)")[1]);
                                }
                            }
                        }
                        eachFileSeq.add(String.valueOf(stringBuilder));
                    }

                    count ++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            fileNameToPositionList.put(eachModificationFile.getName().split("Sites")[0].replace("_", ">"), eachFile);
            fileNameToSeqList.put(eachModificationFile.getName().split("Sites")[0].replace("_", ">"), eachFileSeq);
        }
    }

    /**
     * Get all modification peptide
     */
    public void getModificationPeptides() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(modificationSpecificPeptidesFile));

        String line;
        String[] values;
        Integer count = 0;
        ArrayList<String> eachIDs;
        HashMap<String, ArrayList<String>> modificationFileToEachSite;
        ArrayList<String> eachMod;
        Integer IDsIndex = 0;
        String allIDs;
        HashMap<Integer, String> modificationIndexToFile = new HashMap<>();
        while ((line = bufferedReader.readLine())!=null){

            values = line.split("\t");
            if (count == 0){
                for (Integer index = 0 ; index<values.length; index ++){

                    if(values[index].equals("MS/MS IDs")) {
                        IDsIndex = index;
                    } else if (values[index].contains("site IDs")){
                        modificationIndexToFile.put(index, values[index].split(" site")[0]);
                    }
                }
            } else {

                eachIDs = new ArrayList<>();
                modificationFileToEachSite = new HashMap<>();
                allIDs = values[IDsIndex];

                for (String each : allIDs.split(";")){
                    eachIDs.add(each);
                }

                for (Integer eachModIndex : modificationIndexToFile.keySet()){
                    eachMod = new ArrayList<>();
                    if (eachModIndex < values.length && !values[eachModIndex].equals("") && values[eachModIndex] != null){
                        for (String each : values[eachModIndex].split(";")){
                            eachMod.add(each);
                        }
                        modificationFileToEachSite.put(modificationIndexToFile.get(eachModIndex), eachMod);
                    }

                }
                if (modificationFileToEachSite.size() != 0){
                    msIDSToModificationFileToEachSite.put(eachIDs, modificationFileToEachSite);
                }
            }

            count ++;
        }
    }

    /**
     * Generate all MGF files
     */
    public void generateNewMGFFile(){
        ProgressDialogX progressDialog = new ProgressDialogX(pdvMainClass,
                Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/SeaGullMass.png")),
                Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/SeaGullMassWait.png")),
                true);
        progressDialog.setTitle("Generating spectrum. Please wait...");
        new Thread("GenerateMGFProgress") {
            @Override
            public void run() {
                progressDialog.setVisible(true);

                progressDialog.setPrimaryProgressCounterIndeterminate(true);
            }
        }.start();

        new Thread("GenerateMGF") {
            @Override
            public void run() {
                HashMap<String, BufferedWriter> fileNameToFileWriter = new HashMap<>();
                HashMap<String, ArrayList<String>> fileNameToScanNum = new HashMap<>();
                String parentDictory = maxQuantDirectory.getAbsolutePath() + PDVMainClass.FILE_SEPARATOR +"generatesMGF" + PDVMainClass.FILE_SEPARATOR;
                ArrayList<String> currentScanNumList;

                File mgfDirectory = new File(parentDictory);
                ArrayList<String> allTitle = new ArrayList<>();

                if (!mgfDirectory.exists()) {
                    mgfDirectory.mkdir();
                }

                try {
                    progressDialog.setMaxPrimaryProgressCounter(allAPLFiles.size() + allSilAPLFiles.size());
                    for (String fileName : fileToTitleToRTMap.keySet()) {
                        BufferedWriter fileWriter = new BufferedWriter(new FileWriter(new File(parentDictory + fileName + ".mgf")));
                        fileNameToFileWriter.put(fileName, fileWriter);
                        ArrayList<String> eachFileScanList = new ArrayList<>();
                        fileNameToScanNum.put(fileName, eachFileScanList);
                    }

                    BufferedWriter fileWriter = null;
                    String spectrumTitle;
                    String spectrumFileName;
                    String scanNum;
                    String mass = null;
                    String charge = null;
                    boolean isFirstMentioned = true;

                    for (File eachAPLFile : allAPLFiles) {

                        BufferedReader bufferedReader = new BufferedReader(new FileReader(eachAPLFile));
                        String line;
                        boolean insideSpectrum = false;

                        while ((line = bufferedReader.readLine()) != null) {

                            if (line.endsWith("\r")) {
                                line = line.replace("\r", "");
                            }

                            if (line.startsWith("peaklist start")) {
                                insideSpectrum = true;

                            } else if (line.startsWith("header")) {

                                spectrumTitle = line.split(" Precursor")[0].split("=")[1];
                                spectrumFileName = spectrumTitle.split(" ")[1];
                                scanNum = spectrumTitle.split(" ")[3];

                                spectrumTitle = "RawFile: " + spectrumFileName + " Index: " + scanNum;

                                    currentScanNumList = fileNameToScanNum.get(spectrumFileName);

                                    if (currentScanNumList != null) {

                                        if (currentScanNumList.contains(scanNum)) {
                                            isFirstMentioned = false;
                                        } else {
                                            fileNameToScanNum.get(spectrumFileName).add(scanNum);
                                            isFirstMentioned = true;
                                        }

                                        if (isFirstMentioned) {
                                            long start2 = System.currentTimeMillis();
                                            fileWriter = fileNameToFileWriter.get(spectrumFileName);

                                            fileWriter.write("BEGIN IONS\n");
                                            fileWriter.write("TITLE=" + spectrumTitle + "\n");
                                            fileWriter.write("PEPMASS=" + mass + "\n");
                                            fileWriter.write("CHARGE=" + charge + "+\n");
                                            fileWriter.write("RTINSECONDS=" + Double.valueOf(fileToTitleToRTMap.get(spectrumFileName).get(spectrumTitle)) * 60 + "\n");

                                        }
                                    } else {
                                        System.err.println("It can not find this file ");
                                        isFirstMentioned = false;
                                    }

                            } else if (line.startsWith("mz")) {
                                mass = line.split("=")[1];

                            } else if (line.startsWith("charge")) {
                                charge = line.split("=")[1];

                            } else if (line.startsWith("peaklist end")) {
                                if (isFirstMentioned) {
                                    fileWriter.write("END IONS\n");
                                }

                            } else if (line.startsWith("fragmentation")) {

                            } else if (insideSpectrum && !line.equals("")) {
                                if (isFirstMentioned) {
                                    fileWriter.write(line + "\n");
                                }
                            }
                        }
                        bufferedReader.close();

                        progressDialog.increasePrimaryProgressCounter();
                    }

                    for (File file : allSilAPLFiles){

                        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                        String line;
                        boolean insideSpectrum = false;

                        while ((line = bufferedReader.readLine()) != null) {

                            if (line.endsWith("\r")) {
                                line = line.replace("\r", "");
                            }

                            if (line.startsWith("peaklist start")) {
                                insideSpectrum = true;

                            } else if (line.startsWith("header")) {
                                spectrumTitle = line.split("=")[1];
                                spectrumFileName = spectrumTitle.split(" ")[1];
                                scanNum = spectrumTitle.split(" ")[3];

                                spectrumTitle = "RawFile: " + spectrumFileName + " Index: " + scanNum;

                                    currentScanNumList = fileNameToScanNum.get(spectrumFileName);

                                    if (currentScanNumList != null) {

                                        if (currentScanNumList.contains(scanNum)) {
                                            isFirstMentioned = false;
                                        } else {
                                            fileNameToScanNum.get(spectrumFileName).add(scanNum);
                                            isFirstMentioned = true;
                                        }

                                        if (isFirstMentioned) {
                                            fileWriter = fileNameToFileWriter.get(spectrumFileName);

                                            fileWriter.write("BEGIN IONS\n");
                                            fileWriter.write("TITLE=" + spectrumTitle + "\n");
                                            fileWriter.write("PEPMASS=" + mass + "\n");
                                            fileWriter.write("CHARGE=" + charge + "+\n");
                                            fileWriter.write("RTINSECONDS=" + Double.valueOf(fileToTitleToRTMap.get(spectrumFileName).get(spectrumTitle)) * 60 + "\n");
                                        }
                                    } else {
                                        System.err.println("It can not find this file ");
                                        isFirstMentioned = false;
                                    }

                            } else if (line.startsWith("mz")) {
                                mass = line.split("=")[1];

                            } else if (line.startsWith("charge")) {
                                charge = line.split("=")[1];

                            } else if (line.startsWith("peaklist end")) {
                                if (isFirstMentioned) {
                                    fileWriter.write("END IONS\n");
                                }

                            } else if (line.startsWith("fragmentation")) {

                            } else if (insideSpectrum && !line.equals("")) {
                                if (isFirstMentioned) {
                                    fileWriter.write(line + "\n");
                                }
                            }

                        }

                        bufferedReader.close();
                        progressDialog.increasePrimaryProgressCounter();

                    }

                    fileWriter.close();

                    progressDialog.setRunFinished();

                    fileToTitleToRTMap.clear();

                    for(String fileName : fileNameToFileWriter.keySet()){

                        fileNameToFileWriter.get(fileName).close();

                        System.out.println(fileName+" = "+fileNameToScanNum.get(fileName).size());
                    }

                    addAllSpectrumFiles();

                } catch (IOException |Error e) {

                    if(e.getClass().getName().contains("Error")){
                        progressDialog.setRunFinished();
                        MaxQuantFileImport.this.progressDialog.setRunFinished();
                        JOptionPane.showMessageDialog(pdvMainClass, "Please increase JVM memory! ", "Memory Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        progressDialog.setRunFinished();
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    /**
     * Add mgf into spectrumFactory
     */
    public void addAllSpectrumFiles(){
        SpectrumFactory spectrumFactory = SpectrumFactory.getInstance();

        String parentDictory = maxQuantDirectory.getAbsolutePath() + PDVMainClass.FILE_SEPARATOR + "generatesMGF";
        File mgfDirectory = new File(parentDictory);

        ProgressDialogX progressDialog = new ProgressDialogX(pdvMainClass,
                Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/SeaGullMass.png")),
                Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/SeaGullMassWait.png")),
                true);

        progressDialog.setTitle("Parsing spectrum. Please wait...");
        new Thread("ParseMGFProgress") {
            @Override
            public void run() {
                progressDialog.setVisible(true);

                progressDialog.setPrimaryProgressCounterIndeterminate(true);
            }
        }.start();

        new Thread("ParseMGF") {
            @Override
            public void run() {

                if (!mgfDirectory.exists()) {
                    System.err.println("There is no spectrum directory!");
                    JOptionPane.showMessageDialog(pdvMainClass, "Please check mgf files ", "File Error", JOptionPane.WARNING_MESSAGE);
                    progressDialog.setRunFinished();
                } else {

                    FileFilter fileFilter = new FileFilter() {
                        @Override
                        public boolean accept(File pathname) {
                            return pathname.getName().toLowerCase().endsWith(".mgf");
                        }
                    };
                    File[] allFiles = mgfDirectory.listFiles(fileFilter);

                    if (allFiles == null) {
                        System.err.println("There is no spectrum file!");
                        JOptionPane.showMessageDialog(pdvMainClass, "Please check mgf files ", "File Error", JOptionPane.WARNING_MESSAGE);
                    } else {
                        progressDialog.setMaxPrimaryProgressCounter(allFiles.length);
                        try {
                            for (File eachSpectrumFile : allFiles) {
                                progressDialog.increasePrimaryProgressCounter();

                                spectrumFactory.addSpectra(eachSpectrumFile);

                            }

                            pdvMainClass.setSpectrumFactory(spectrumFactory);

                            progressDialog.setRunFinished();

                            parseResults();
                        } catch (IOException | Error e ) {
                            if(e.getClass().getName().contains("Error")){
                                progressDialog.setRunFinished();
                                MaxQuantFileImport.this.progressDialog.setRunFinished();
                                JOptionPane.showMessageDialog(pdvMainClass, "Please increase JVM memory! ", "Memory Error", JOptionPane.ERROR_MESSAGE);
                            } else {
                                progressDialog.setRunFinished();
                                e.printStackTrace();
                            }
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                    }

                }
            }
        }.start();
    }

    /**
     * Return original information
     * @return HashMap
     */
    public HashMap<String, Object> getOriginalInfor(){
        HashMap<String, Object> originalInfor = new HashMap<>();

        if(parametersFile != null){
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(parametersFile));

                HashMap<String, String> parameterMap = new HashMap<>();

                String line;
                String[] values;

                while ((line = bufferedReader.readLine()) != null) {
                    values = line.split("\t");
                    if(values.length == 2){
                        parameterMap.put(values[0], values[1]);

                        if (values[0].equals("Fixed modifications")){
                            for (String eachMod : values[1].split("; ")){
                                String aa = eachMod.split(" \\(")[1].replace(")", "");
                                fixedModificationMap.put(aa, eachMod.split(" \\(")[0]);

                                allModifications.add(eachMod.split(" \\(")[0] + " of " + aa);
                            }
                        }

                    } else {
                        parameterMap.put(values[0], "");
                    }
                }

                originalInfor.put("Parameters", parameterMap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return originalInfor;
    }

    /**
     * Get all experiment details
     * @return ArrayList
     */
    public ArrayList<String> getDetailsList(){
        ArrayList<String> detailsList = new ArrayList<>();

        if(parametersFile != null) {
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(parametersFile));
                String line;
                String[] values;

                while ((line = bufferedReader.readLine()) != null) {
                    values = line.split("\t");
                    if (!values[0].equals("Parameter")) {
                        if (values.length == 2) {
                            detailsList.add(values[0] + "/t/" + values[1]);
                        } else {
                            detailsList.add(values[0] + "/t/ ");
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return detailsList;
    }

    /**
     * Parsing file
     * @throws SQLException
     * @throws IOException
     */
    private void parseResults() throws SQLException, IOException {

        Connection connection = sqLiteConnection.getConnection();

        connection.setAutoCommit(false);
        Statement statement = connection.createStatement();
        String matchTableQuery = "CREATE TABLE SpectrumMatch (PSMIndex INT(10), MZ DOUBLE, Title Char, Sequence Char, MassError DOUBLE, Match Object, Score DOUBLE, PRIMARY KEY(PSMIndex))";

        try {
            statement.execute(matchTableQuery);
        }catch (SQLException e){
            progressDialog.setRunFinished();
            System.err.println("An error occurred while creating table SpectrumMatch");
            throw (e);
        }finally {
            statement.close();
        }

        String addDataIntoTable = "INSERT INTO SpectrumMatch VALUES(?,?,?,?,?,?,?)";
        PreparedStatement preparedStatement = null;

        BufferedReader bufferedReader = new BufferedReader(new FileReader(msResultFile));

        String line;
        String[] values;
        String spectrumTitle;
        String modificationName;
        String sequence;
        String modificationSequence;
        String scanNumber;
        Integer scanIndex;
        String rawFileName;
        Integer peptideCharge;
        Double massError;
        Double score;
        String utilitiesModificationName;
        String[] splitAABy;
        String[] modifications;
        String[] splitNum;
        PeptideAssumption peptideAssumption;
        Peptide peptide;
        Charge charge;
        HashMap<String, Integer> modificationNumIndex = new HashMap<>();
        HashMap<String, Integer> modificationPosIndex = new HashMap<>();
        HashMap<Integer, Double> modIndexToPos;

        ArrayList<String> spectrumList = new ArrayList<>();
        HashMap<String, ArrayList<String>> fileNameToIDs;
        HashMap<String, String> aAToModification;
        ArrayList<ModificationMatch> utilitiesModifications;
        SpectrumMatch currentMatch;
        ByteArrayOutputStream bos;

        int lineCount = 0;
        int count = 0;
        int countRound = 0;

        while ((line = bufferedReader.readLine()) != null) {
            values = line.split("\t");

            if (lineCount == 0) {
                for (int index = 0; index < values.length; index++) {
                    if(values[index].equals("Raw file")){
                        rawFileIndex = index;
                    } else if(values[index].equals("Scan number")){
                        scanNumIndex = index;
                    } else if(values[index].equals("Scan index")){
                        scanIndexIndex = index;
                    } else if(values[index].equals("Retention time")){

                    } else if(values[index].equals("Sequence")){
                        sequenceIndex = index;
                    } else if(values[index].equals("Modifications")){
                        modificationIndex = index;
                    } else if(values[index].equals("Mass Error [ppm]")){
                        massErrorIndex = index;
                    } else if(values[index].equals("Modified sequence")){
                        modificationSequenceIndex = index;
                    } else if(values[index].equals("Score")){
                        scoreIndex = index;
                    } else if(values[index].equals("Charge")){
                        chargeIndex = index;
                    } else if (variedModifications.contains(values[index])){
                        modificationNumIndex.put(values[index], index);
                    } else if (values[index].contains("Probabilities")){
                        modificationPosIndex.put(values[index].split(" Probabilities")[0], index);
                    }
                }
            } else {

                if (count == 0){
                    preparedStatement = connection.prepareStatement(addDataIntoTable);
                }

                utilitiesModifications = new ArrayList<>();

                sequence = values[sequenceIndex];
                modificationName = values[modificationIndex];
                modificationSequence = values[modificationSequenceIndex];
                if(values[massErrorIndex].contains(".")){
                    massError = Double.valueOf(values[massErrorIndex]);
                } else {
                    massError = 0.0;
                }
                peptideCharge = Integer.valueOf(values[chargeIndex]);
                rawFileName = values[rawFileIndex];
                scanNumber = values[scanNumIndex];
                scanIndex = Integer.valueOf(values[scanIndexIndex]) + fileToCountMap.get(rawFileName);
                score = Double.valueOf(values[scoreIndex]);

                spectrumTitle = "RawFile: " + rawFileName + " Index: " + scanNumber;

                if(spectrumTitleToRank.containsKey(spectrumTitle)){
                    int rank = spectrumTitleToRank.get(spectrumTitle) + 1;
                    spectrumTitleToRank.put(spectrumTitle, rank);

                    currentMatch = allSpectrumMatches.get(scanIndex);

                } else{
                    spectrumTitleToRank.put(spectrumTitle, 1);

                    currentMatch = new SpectrumMatch(Spectrum.getSpectrumKey(rawFileName+".mgf", spectrumTitle));
                    currentMatch.setSpectrumNumber(scanIndex);
                    allSpectrumMatches.put(scanIndex, currentMatch);
                }

                try {
                    if (!modificationName.equals("Unmodified")) {
                        /*
                        for (ArrayList<String> eachList : msIDSToModificationFileToEachSite.keySet()){
                            if (eachList.contains(String.valueOf(lineCount - 1))){
                                fileNameToIDs = msIDSToModificationFileToEachSite.get(eachList);

                                for (String fileName : fileNameToIDs.keySet()) {
                                    for (String eachID : fileNameToIDs.get(fileName)) {
                                        Integer site = Integer.valueOf(fileNameToPositionList.get(fileName).get(Integer.parseInt(eachID)));
                                        String modSeq = fileNameToSeqList.get(fileName).get(Integer.parseInt(eachID));
                                        if (! modSeq.equals(sequence)) {
                                            if (modSeq.length() > sequence.length()) {
                                                if (modSeq.contains(sequence)){
                                                    site = site - modSeq.split(sequence)[0].length();
                                                } else {
                                                    String origin = "";
                                                    for (int i = 0; i < sequence.length(); i++) {
                                                        for (int j = sequence.length(); j > i; j--) {
                                                            String s3 = sequence.substring(i, j);
                                                            if (modSeq.contains(s3)) {
                                                                if (s3.length() > origin.length()){
                                                                    origin = s3;
                                                                }
                                                            }
                                                        }
                                                    }
                                                    site = site - modSeq.split(origin)[0].length() + sequence.split(origin)[0].length();
                                                }
                                            } else {
                                                if (sequence.contains(modSeq)){
                                                    site = site + sequence.split(modSeq)[0].length();
                                                } else {
                                                    String origin = "";
                                                    for (int i = 0; i < modSeq.length(); i++) {
                                                        for (int j = modSeq.length(); j > i; j--) {
                                                            String s3 = modSeq.substring(i, j);
                                                            if (sequence.contains(s3)) {
                                                                if (s3.length() > origin.length()){
                                                                    origin = s3;
                                                                }
                                                            }
                                                        }
                                                    }
                                                    site = site - modSeq.split(origin)[0].length() + sequence.split(origin)[0].length();
                                                }
                                            }
                                        }
                                        utilitiesModificationName = fileName.split(" \\(")[0].replace(">","&gt;") + " of " + sequence.charAt(site - 1);

                                        utilitiesModifications.add(new ModificationMatch(utilitiesModificationName, true, site));
                                        if (!allModifications.contains(utilitiesModificationName)) {
                                            allModifications.add(utilitiesModificationName);
                                        }
                                    }
                                }
                            }
                        } */

                        for (String modName : modificationNumIndex.keySet()){

                            Integer modNumIndex = modificationNumIndex.get(modName);

                            Integer modNum = Integer.valueOf(values[modNumIndex]);

                            Integer modPosIndex = modificationPosIndex.get(modName);

                            String modPosSequence = values[modPosIndex];

                            modIndexToPos = new HashMap<>();

                            if (modNum != 0){

                                String[] firstSplitList = modPosSequence.split("\\)");

                                Integer accumPosition = 0;
                                String splitSequence;
                                Double splitPos;

                                for (String firstSplit : firstSplitList){

                                    if (firstSplit.contains("(")){
                                        splitSequence = firstSplit.split("\\(")[0];
                                        splitPos = Double.valueOf(firstSplit.split("\\(")[1]);

                                        accumPosition += splitSequence.length();

                                        modIndexToPos.put(accumPosition, splitPos);
                                    }
                                }

                                List<Map.Entry<Integer, Double>> list = new ArrayList<>(modIndexToPos.entrySet());
                                Collections.sort(list, Comparator.comparing(Map.Entry::getKey));

                                Collections.sort(list, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));

                                for (int i = 0; i < modNum; i ++){

                                    Integer position = list.get(i).getKey();
                                    utilitiesModificationName = modName.split(" \\(")[0].replace(">","&gt;") + " of " + sequence.charAt(position - 1);

                                    utilitiesModifications.add(new ModificationMatch(utilitiesModificationName, true, position));
                                    if (!allModifications.contains(utilitiesModificationName)) {
                                        allModifications.add(utilitiesModificationName);
                                    }
                                }

                            }
                        }

                        aAToModification = new HashMap<>();

                        modifications = modificationName.split(",");

                        for (String eachModification : modifications) {
                            splitNum = eachModification.split(" ");
                            if (eachModification.contains("C-term")) {
                                if (eachModification.contains("Protein C-term")){
                                    aAToModification.put("Protein C-term", splitNum[0]);
                                } else {
                                    aAToModification.put("C-term", splitNum[0]);
                                }
                            } else if (eachModification.contains("N-term")){
                                if (eachModification.contains("Protein N-term")){
                                    aAToModification.put("Protein N-term", splitNum[0]);
                                } else {
                                    aAToModification.put("N-term", splitNum[0]);
                                }
                            }
                        }

                        splitAABy = modificationSequence.split("\\(");

                        for (String eachModificationPart : splitAABy) {
                            if (eachModificationPart.equals("_")) {
                                if (aAToModification.get("N-term") == null){
                                    utilitiesModificationName = aAToModification.get("Protein N-term") + " of " + "protein N-term";
                                } else {
                                    utilitiesModificationName = aAToModification.get("N-term") + " of " + "N-term";
                                }
                                if (!allModifications.contains(utilitiesModificationName)) {
                                    allModifications.add(utilitiesModificationName);
                                }
                                utilitiesModifications.add(new ModificationMatch(utilitiesModificationName, true, 1));
                            } else if (eachModificationPart == splitAABy[splitAABy.length - 1] && !eachModificationPart.contains("_")) {
                                if (aAToModification.get("C-term") == null){
                                    utilitiesModificationName = aAToModification.get("Protein C-term") + " of " + "protein C-term";
                                } else {
                                    utilitiesModificationName = aAToModification.get("C-term") + " of " + "C-term";
                                }
                                if (!allModifications.contains(utilitiesModificationName)) {
                                    allModifications.add(utilitiesModificationName);
                                }
                                utilitiesModifications.add(new ModificationMatch(utilitiesModificationName, true, sequence.length()));
                            }
                        }
                    }
                } catch (Exception e){
                    progressDialog.setRunFinished();
                    JOptionPane.showMessageDialog(
                            null, "Modification format not support.\n" + e.toString(),
                            "Error Modification", JOptionPane.ERROR_MESSAGE);
                }

                String[] sequenceArray = sequence.split("");
                int length =  sequenceArray.length;
                for (int index = 0; index < length; index ++){
                    String aa = sequenceArray[index];
                    if (fixedModificationMap.containsKey(aa)){
                        utilitiesModificationName = fixedModificationMap.get(aa) + " of " + aa;
                        utilitiesModifications.add(new ModificationMatch(utilitiesModificationName, true, index + 1));
                    }
                }

                peptide = new Peptide(sequence, utilitiesModifications);

                charge = new Charge(+1, peptideCharge);

                peptideAssumption = new PeptideAssumption(peptide, spectrumTitleToRank.get(spectrumTitle), 0, charge, massError, "maxQuant");
                peptideAssumption.setRawScore(score);

                currentMatch.addHit(0, peptideAssumption, false);

                if(spectrumTitleToRank.get(spectrumTitle) == 1){
                    currentMatch.setBestPeptideAssumption(peptideAssumption);

                    spectrumList.add(String.valueOf(scanIndex));

                    bos = new ByteArrayOutputStream();
                    try {
                        ObjectOutputStream oos = new ObjectOutputStream(bos);
                        try {
                            oos.writeObject(currentMatch);
                        } finally {
                            oos.close();
                        }
                    } finally {
                        bos.close();
                    }

                    preparedStatement.setInt(1, scanIndex);
                    preparedStatement.setDouble(2, peptideAssumption.getTheoreticMass());
                    preparedStatement.setString(3, spectrumTitle);
                    preparedStatement.setString(4, sequence);
                    preparedStatement.setDouble(5, Math.abs(massError));
                    preparedStatement.setBytes(6, bos.toByteArray());
                    preparedStatement.setDouble(7, score);
                    
                    preparedStatement.addBatch();

                    count ++;

                    if(count == 1000){
                        int[] counts = preparedStatement.executeBatch();
                        connection.commit();
                        preparedStatement.close();

                        pdvMainClass.allSpectrumIndex.add(spectrumList);

                        count = 0;

                        if(countRound == 0){

                            pdvMainClass.displayResult();
                            pdvMainClass.pageNumJTextField.setText(1 + "/" + 1);
                            progressDialog.setRunFinished();

                            countRound ++;

                        } else {
                            pdvMainClass.pageNumJTextField.setText(String.valueOf(pdvMainClass.selectedPageNum) + "/" + String.valueOf(pdvMainClass.allSpectrumIndex.size()));
                            countRound ++;
                        }

                        spectrumList = new ArrayList<>();
                    }
                }
            }

            lineCount ++;
            
        }
        bufferedReader.close();

        if(count != 0){

            int[] counts = preparedStatement.executeBatch();
            connection.commit();
            preparedStatement.close();

            pdvMainClass.allSpectrumIndex.add(spectrumList);

            if(countRound == 0){

                pdvMainClass.displayResult();
                pdvMainClass.pageNumJTextField.setText(1 + "/" + 1);
                progressDialog.setRunFinished();

            } else {
                pdvMainClass.pageNumJTextField.setText(String.valueOf(pdvMainClass.selectedPageNum) + "/" + String.valueOf(pdvMainClass.allSpectrumIndex.size()));
            }

        }

        pdvMainClass.loadingJButton.setIcon(new ImageIcon(getClass().getResource("/icons/done.png")));
        pdvMainClass.loadingJButton.setText("Import done");
        pdvMainClass.searchButton.setToolTipText("Find items");
        pdvMainClass.searchItemTextField.setToolTipText("Find items");
    }

    /**
     * Return SQLiteConnection
     * @return SQLiteConnection
     */
    public SQLiteConnection getSqLiteConnection(){
        return sqLiteConnection;
    }

    /**
     * Return all modification
     * @return array list
     */
    public ArrayList<String> getAllModifications(){
        return allModifications;
    }
}
