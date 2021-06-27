

public class SmokerProblem {
    static final int InterruptZeit = 5000;   
    public final int MAX_IDLE_TIME = 100;
    public final String TABACCO = "Tabak";
    public final String PAPER = "Papier";
    public final String LIGHTER = "Streichhoelzer";
    static Table Tisch = new Table();
    static List<String> ZutatenListe = new ArrayList<String>();

    public static void main() {
        ZutatenListe.add(TABACCO);
        ZutatenListe.add(PAPER);
        ZutatenListe.add(LIGHTER);

        LinkedList<Agent> producerList = new LinkedList<Agent>();
        LinkedList<Smoker> consumerList = new LinkedList<Smoker>();

        // Verbraucher - Threads erzeugen
        for (int i = 1; i <= ZutatenListe.size(); i++) {
            Smoker smoker = new Consumer(ZutatenListe[i-1]);
            smoker.setName("Raucher " + i + " (Hat " + ZutatenListe[i-1] + ")");
            consumerList.add(smoker);
            smoker.start();
        }
        
        // Erzeuger - Threads erzeugen
        for (int i = 1; i <= 2; i++) {
            Agent agent = new Producer();
            agent.setName("Agent " + i);
            producerList.add(agent);
            agent.start();
        }

        // Laufzeit abwarten
        try {
            Thread.sleep(InterruptZeit);

            System.err.println("-------------------- ENDE -------------------");

            // Erzeuger - Threads stoppen
            for (Producer current : producerList) {
                current.interrupt();
            }

            // Verbraucher - Threads stoppen
            for (Consumer current : consumerList) {
                current.interrupt();
            }

        } catch (InterruptedException e) {
        }
    }

    class Table {
        private Set<String> ZutatenSet;

        Table() {
            this.ZutatenSet = new Set<String>();
        }

        /* Producer (Agent) rufen die Methode gebeZutaten auf */
        public synchronized void gebeZutaten(String ZutatA, String ZutatB) throws InterruptedException {
            while (this.ZutatenSet.size() != 0) {
                this.wait(); // --> Warten in der Wait-Queue
            }

            // 2 Zutaten auf den Tisch legen
            System.err.println(Thread.currentThread().getName() + " legt die Zutaten " + ZutatA + " und " + ZutatB + " auf den Tisch");
            this.ZutatenSet.add(ZutatA);
            this.ZutatenSet.add(ZutatB);

            this.notifyAll();
        }

        /* Consumer (Smoker) rufen die Methode verbraucheZutaten auf */
        public synchronized boolean verbraucheZutaten(String EigeneZutat) throws InterruptedException {
            while(this.ZutatenSet.size() == 0) {
                this.wait(); // --> Warten in der Wait-Queue
            } 

            if (!this.ZutatenSet.add(EigeneZutat)) { // Prüfen ob es nicht die gesuchten Zutaten sind
                this.notifyAll();
                return false;
            }

            // Es waren die gewollten Zutaten
            System.err.println(Thread.currentThread().getName() + " bereitet sich eine Zigarette vor und raucht sie dann");
            Thread.sleep((int) (MAX_IDLE_TIME * Math.random()));
            this.ZutatenSet.clear();

            this.notifyAll();
            return true;
        }
    }

    class Smoker extends Thread {
        private String EigeneZutat;
        
        Smoker(String EigeneZutat) {
            this.EigeneZutat = EigeneZutat;
        }

        public void run() {
            try {
                while (!isInterrupted()) {
                    System.err.println(this.getName() + " moechte auf den Tisch zugreifen!");
                    if (currentBuffer.verbraucheZutaten(this.EigeneZutat, pause())) {
                        System.err.println(this.getName() + " hat genüsslich eine geraucht!");
                    }
                    else {
                        System.err.println(this.getName() + " hatte leider nicht alle Zutaten und muss wieder warten!");
                    }
                }
            } catch (InterruptedException ex) {
                System.err.println(this.getName() + " wurde erfolgreich interrupted!");
            }
        }
    }

    class Agent extends Thread {
        public void run() {
            try {
                while (!isInterrupted()) {
                    System.err.println(this.getName() + " moechte auf den Tisch ablegen!");

                    List<String> zutatencopy = new ArrayList<String>(ZutatenListe);
                    Collections.shuffle(zutatencopy);
 
                    currentBuffer.gebeZutaten(zutatencopy[0], zutatencopy[1]);
                }
            } catch (InterruptedException ex) {
                System.err.println(this.getName() + " wurde erfolgreich interrupted!");
            }
        }
    }
}