import java.io.*;
import java.util.*;

public class HuffmanCode implements Huffman{
    private String pathName;
    private BinaryTree root;
    private Map<Character, Long> charFrequencies;
//    private final Map<Character, String> huffmanCodes;

    public HuffmanCode(String input, String output){
        this.pathName= pathName;
    }



   public  Map<Character, Long> countFrequencies(String pathName) throws IOException{
        charFrequencies= new HashMap<>();

        BufferedReader input;


       // Open the file, if possible
       try {
           input = new BufferedReader(new FileReader(pathName));
       }
       catch (FileNotFoundException e) {
           System.err.println("Cannot open file.\n" + e.getMessage());
           return charFrequencies;
       }


       // Read the file
       try {
           // Line by line
           String line;
           while ((line = input.readLine()) != null) {

                char[] arr= line.toCharArray();
                for(char c: arr) {

                  if(charFrequencies.containsKey(c)){
                      charFrequencies.put(c,charFrequencies.get(c)+1);
                  }
                  else{
                      charFrequencies.put(c,1L);
                  }
              }
           }
           return charFrequencies;
       }
       finally {
           // Close the file, if possible
           try {
               input.close();
           }
           catch (IOException e) {
               System.err.println("Cannot close file.\n" + e.getMessage());
           }
       }


   }


   public BinaryTree<CodeTreeElement> makeCodeTree(Map<Character, Long> frequencies){
        Comparator<BinaryTree<CodeTreeElement>> frequencyComparator = new BinaryTreeCompare();

       Queue<BinaryTree<CodeTreeElement>> queue= new PriorityQueue<BinaryTree<CodeTreeElement>>(frequencyComparator);
       charFrequencies.forEach((character,frequency)-> queue.add(new BinaryTree<CodeTreeElement>(new CodeTreeElement( frequency,character))));
        if (queue.isEmpty()){
            return null;
        }
       if(queue.size()==1){
           BinaryTree<CodeTreeElement> t = queue.poll();
           CodeTreeElement frequency= new CodeTreeElement(t.getData().getFrequency(),null);
           BinaryTree<CodeTreeElement> t1= new BinaryTree<CodeTreeElement>(frequency,null,t);
           queue.add(t1);
       }
       while(queue.size()>1){
           BinaryTree<CodeTreeElement> T1= queue.poll();
           BinaryTree<CodeTreeElement> T2= queue.poll();

           Long newFrequency = T1.getData().getFrequency() + T2.getData().getFrequency();

           CodeTreeElement T = new CodeTreeElement(newFrequency);

           queue.add(new BinaryTree<CodeTreeElement>(T,T1,T2));
       }

       BinaryTree<CodeTreeElement> codeTree = queue.poll();
       return codeTree;
   }




   public Map<Character, String> computeCodesHelper(BinaryTree<CodeTreeElement> codeTree,String huffCode,Map<Character, String> huffmanCodes){

        if(codeTree.isLeaf()){
            huffmanCodes.put(codeTree.getData().getChar(),huffCode);
            return huffmanCodes;
        }
        if(codeTree.hasLeft()){
            computeCodesHelper(codeTree.getLeft(), huffCode.concat("0"), huffmanCodes);
        }
        if (codeTree.hasRight()){
            computeCodesHelper(codeTree.getRight(), huffCode.concat("1"), huffmanCodes);
        }

        return huffmanCodes;
   }

   public Map<Character, String> computeCodes(BinaryTree<CodeTreeElement> codeTree){
        HashMap<Character, String> codeMap = new HashMap<>();
        if (codeTree!= null){
            computeCodesHelper(codeTree,"",codeMap);
        }
        return codeMap;
   }


   public void compressFile(Map<Character, String> codeMap, String pathName, String compressedPathName) throws IOException{
        if (codeMap.isEmpty()) return;

       // Open the file, if possible
       try {
           BufferedReader input = new BufferedReader(new FileReader(pathName));

           BufferedBitWriter bitOutput = new BufferedBitWriter(compressedPathName);
           int current = input.read();

           while (current !=-1){
               Character c= (char) current;
               String val=codeMap.get(c);

               if (val != null){
                   for (int i=0; i< val.length(); i++){
                       bitOutput.writeBit(val.charAt(i)=='1');
                   }
               }

               current = input.read();
           }

           input.close();
           bitOutput.close();
       }
       catch (FileNotFoundException e) {
           System.err.println("Cannot open file.\n" + e.getMessage());

       }


   }

    public void decompressFile(String compressedPathName, String decompressedPathName, BinaryTree<CodeTreeElement> codeTree) throws IOException{
        try{
            BufferedBitReader bitInput = new BufferedBitReader(compressedPathName);
            BufferedWriter output = new BufferedWriter(new FileWriter(decompressedPathName));

            BinaryTree<CodeTreeElement> temp= codeTree;
            if (!bitInput.hasNext()) return;


            while (bitInput.hasNext()) {
                boolean bit = bitInput.readBit();
                if (bit){
                    temp=temp.getRight();
                }
                else if (!bit){
                    temp=temp.getLeft();
                }

                if (temp.isLeaf()){
                    output.write(temp.getData().getChar());
                    temp= codeTree;
                }
            }

            bitInput.close();
            output.close();
        }
        catch (FileNotFoundException e){
            System.err.println("Cannot open file.\n" + e.getMessage());
        }

    }

    public static void main(String[] args) throws IOException{

//        USconstitution
        String pathName= "/Users/abdul-kudusalhassan/IdeaProjects/CS10/untitled/PS3/USConstitution.txt";
        String outputFile="/Users/abdul-kudusalhassan/IdeaProjects/CS10/untitled/PS3/USConst_compress";

        String decompressed = "/Users/abdul-kudusalhassan/IdeaProjects/CS10/untitled/PS3/USConst_decompress" ;



        HuffmanCode obj= new HuffmanCode(pathName, outputFile);
        Map<Character, Long> map= obj.countFrequencies(pathName);

        BinaryTree<CodeTreeElement> codeTree = obj.makeCodeTree(map);
        Map<Character, String> codeMap=obj.computeCodes(codeTree);


        obj.compressFile(codeMap,pathName,outputFile);
        obj.decompressFile(outputFile,decompressed,codeTree);
//WarAndPeace

        String pathName1= "/Users/abdul-kudusalhassan/IdeaProjects/CS10/untitled/PS3/WarAndPeace.txt";
        String outputFile1="/Users/abdul-kudusalhassan/IdeaProjects/CS10/untitled/PS3/WarAndPeace_compress";

        String decompressed1 = "/Users/abdul-kudusalhassan/IdeaProjects/CS10/untitled/PS3/WarAndPeace_decompress" ;


        HuffmanCode obj1= new HuffmanCode(pathName1, outputFile1);
        Map<Character, Long> map1= obj.countFrequencies(pathName1);

        BinaryTree<CodeTreeElement> codeTree1 = obj.makeCodeTree(map1);
        Map<Character, String> codeMap1=obj.computeCodes(codeTree1);


        obj1.compressFile(codeMap1,pathName1,outputFile1);
        obj1.decompressFile(outputFile1,decompressed1,codeTree1);

//TestFile

        String pathName0= "/Users/abdul-kudusalhassan/IdeaProjects/CS10/untitled/PS3/testfile";
        String outputFile0="/Users/abdul-kudusalhassan/IdeaProjects/CS10/untitled/PS3/test_compress";

        String decompressed0 = "/Users/abdul-kudusalhassan/IdeaProjects/CS10/untitled/PS3/test_decompress" ;


        HuffmanCode obj0= new HuffmanCode(pathName0, outputFile0);
        Map<Character, Long> map0= obj.countFrequencies(pathName0);

        BinaryTree<CodeTreeElement> codeTree0 = obj.makeCodeTree(map0);
        Map<Character, String> codeMap0=obj.computeCodes(codeTree0);


        obj0.compressFile(codeMap0,pathName0,outputFile0);
        obj0.decompressFile(outputFile0,decompressed0,codeTree0);


    }


}
