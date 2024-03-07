import java.util.HashMap;
import java.util.Random;

public class LanguageModel {

    // The map of this model.
    // Maps windows to lists of charachter data objects.
    HashMap<String, List> CharDataMap;
    
    // The window length used in this model.
    int windowLength;
    
    // The random number generator used by this model. 
	private Random randomGenerator;

    /** Constructs a language model with the given window length and a given
     *  seed value. Generating texts from this model multiple times with the 
     *  same seed value will produce the same random texts. Good for debugging. */
    public LanguageModel(int windowLength, int seed) {
        this.windowLength = windowLength;
        randomGenerator = new Random(seed);
        CharDataMap = new HashMap<String, List>();
    }

    /** Constructs a language model with the given window length.
     * Generating texts from this model multiple times will produce
     * different random texts. Good for production. */
    public LanguageModel(int windowLength) {
        this.windowLength = windowLength;
        randomGenerator = new Random();
        CharDataMap = new HashMap<String, List>();
    }

    /** Builds a language model from the text in the given file (the corpus). */
	public void train(String fileName) {
		// Your code goes here
		In newReader = new In(fileName);

        String read = newReader.readAll();

        for (int i = 0; i < read.length() - windowLength ; ++i) {
            String key = read.substring(i, i + windowLength);
            if (CharDataMap.get(key) == null) {
                CharDataMap.put(key, new List());
            }
            CharDataMap.get(key).update(read.charAt(i + windowLength));
            calculateProbabilities(CharDataMap.get(key));
        }
	}

    // Computes and sets the probabilities (p and cp fields) of all the
	// characters in the given list. */
	public void calculateProbabilities(List probs) {				
		// Your code goes here
        int totalCharCounter = 0;

        ListIterator lIterator = probs.listIterator(0);
        while (lIterator != null && lIterator.hasNext()) {
            totalCharCounter += lIterator.next().count;
        }

        double cp = 0.0;
        for (int i = 0; i < probs.getSize(); ++i) {
            CharData currentCharData = probs.get(i);
            currentCharData.p = (double)currentCharData.count / totalCharCounter;
            currentCharData.cp = (double)cp + currentCharData.p;
            cp += currentCharData.p;
        }
    }


    // Returns a random character from the given probabilities list.
	public char getRandomChar(List probs) {
		// Your code goes here
        double cpValue = randomGenerator.nextDouble();
        CharData[] charDataArray = probs.toArray();

        for (CharData cd : charDataArray) {
            if (cd.cp > cpValue) {
                return cd.chr;
            }
        }
        return ' ';
	}

    /**
	 * Generates a random text, based on the probabilities that were learned during training. 
	 * @param initialText - text to start with. If initialText's last substring of size numberOfLetters
	 * doesn't appear as a key in Map, we generate no text and return only the initial text. 
	 * @param numberOfLetters - the size of text to generate
	 * @return the generated text
	 */
	public String generate(String initialText, int textLength) {
		// Your code goes here
        StringBuilder strBuild = new StringBuilder();
        strBuild.append(initialText);
        while (strBuild.length() < textLength + initialText.length()) {
            String key = strBuild.substring(strBuild.length() - windowLength);
            strBuild.append(getRandomChar(CharDataMap.get(key)));
        }
        return strBuild.toString();
	}

    /** Returns a string representing the map of this language model. */
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (String key : CharDataMap.keySet()) {
			List keyProbs = CharDataMap.get(key);
			str.append(key + " : " + keyProbs + "\n");
		}
		return str.toString();
	}

    public static void main(String[] args) {
		// Your code goes here
    }
}
