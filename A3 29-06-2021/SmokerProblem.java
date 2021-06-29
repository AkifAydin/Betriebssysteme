package com.company;


import java.util.*;

public class SmokerProblem {
    public static final int InterruptZeit = 5000;
    public static final int MAX_IDLE_TIME = 1000;
    public static final String TABACCO = "Tabak";
    public static final String PAPER = "Papier";
    public static final String LIGHTER = "Streichhoelzer";
    static Table Tisch = new Table();
    static List<String> ZutatenListe = new ArrayList<String>();

    public static void main(String[] args) {
        ZutatenListe.add(TABACCO);
        ZutatenListe.add(PAPER);
        ZutatenListe.add(LIGHTER);

        LinkedList<Agent> producerList = new LinkedList<Agent>();
        LinkedList<Smoker> consumerList = new LinkedList<Smoker>();

        // Verbraucher - Threads erzeugen
        for (int i = 1; i <= ZutatenListe.size(); i++) {
            Smoker smoker = new Smoker(ZutatenListe.get(i-1));
            smoker.setName("Raucher " + i + " (Hat " + ZutatenListe.get(i-1) + ")");
            consumerList.add(smoker);
            smoker.start();
        }

        // Erzeuger - Threads erzeugen
        for (int i = 1; i <= 2; i++) {
            Agent agent = new Agent();
            agent.setName("Agent " + i);
            producerList.add(agent);
            agent.start();
        }

        // Laufzeit abwarten
        try {
            Thread.sleep(InterruptZeit);

            System.err.println("-------------------- ENDE -------------------");

            // Erzeuger - Threads stoppen
            for (Agent current : producerList) {
                current.interrupt();
            }

            // Verbraucher - Threads stoppen
            for (Smoker current : consumerList) {
                current.interrupt();
            }

        } catch (InterruptedException e) {
        }
    }

    static class  Table {
        private Set<String> ZutatenSet;

        Table() {
            this.ZutatenSet = new TreeSet<String>();
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
        public synchronized void verbraucheZutaten(String EigeneZutat) throws InterruptedException {
            while(this.ZutatenSet.size() == 0 || !this.ZutatenSet.add(EigeneZutat)) {
                this.wait(); // --> Warten in der Wait-Queue
            }

            // Es waren die gewollten Zutaten
            System.err.println(Thread.currentThread().getName() + " bereitet sich eine Zigarette vor und raucht sie dann");
            Thread.sleep((int) (MAX_IDLE_TIME * Math.random()));
            this.ZutatenSet.clear();
            System.err.println(Thread.currentThread().getName() + " hat genüsslich eine geraucht!");

            this.notifyAll();
        }
    }

    static class  Smoker extends Thread {
        private String EigeneZutat;

        Smoker(String EigeneZutat) {
            this.EigeneZutat = EigeneZutat;
        }

        public void run() {
            try {
                while (!isInterrupted()) {
                    System.err.println(this.getName() + " moechte auf den Tisch zugreifen!");
                    Tisch.verbraucheZutaten(this.EigeneZutat);
                }
            } catch (InterruptedException ex) {
                System.err.println(this.getName() + " wurde erfolgreich interrupted!");
            }
        }
    }

    static class  Agent extends Thread {
        public void run() {
            try {
                while (!isInterrupted()) {
                    System.err.println(this.getName() + " moechte auf den Tisch ablegen!");

                    List<String> zutatencopy = new ArrayList<String>(ZutatenListe);
                    Collections.shuffle(zutatencopy);

                    Tisch.gebeZutaten(zutatencopy.get(0), zutatencopy.get(1));
                }
            } catch (InterruptedException ex) {
                System.err.println(this.getName() + " wurde erfolgreich interrupted!");
            }
        }
    }
}