package GraphAndAlgo;

import java.io.*;

class GraphTest {

    public static void main(String[] args) throws IOException {
        // testing example from: http://www.ideserve.co.in/learn/bellman-ford-shortest-path-algorithm

        String[] vlist = {"1","2","3","4","5","6"};
        ForexGraph g = new ForexGraph(vlist);
        g.addEdge(0,1,4);
        g.addEdge(1,2,-1);
        g.addEdge(2,5,3);
        g.addEdge(0,3,9);
        g.addEdge(3,2,2);
        g.addEdge(3,4,-5);
        g.addEdge(4,5,0);
        g.addEdge(5,0,-6); // this edge causes nwcycle

        g.displayGraph();

        System.out.println("nwc exists? "+g.detectNWCycle(0));
        System.out.println("cycle: "+g.getNWCycle());
/*
        System.out.println("nwc exists? "+g.detectNWCycle(1)); //PROBLEM
        System.out.println("cycle: "+g.getNWCycle());

        System.out.println("nwc exists? "+g.detectNWCycle(2)); //PROBLEM
        System.out.println("cycle: "+g.getNWCycle());

        System.out.println("nwc exists? "+g.detectNWCycle(3));
        System.out.println("cycle: "+g.getNWCycle());

        System.out.println("nwc exists? "+g.detectNWCycle(4));
        System.out.println("cycle: "+g.getNWCycle());

        System.out.println("nwc exists? "+g.detectNWCycle(5));
        System.out.println("cycle: "+g.getNWCycle());
*/

        System.out.println("ia is possible? "+g.infiniteArbitragePossible(0));
        //System.out.println("ia is possible? "+g.infiniteArbitragePossible(1));
        //System.out.println("ia is possible? "+g.infiniteArbitragePossible(2));
        //System.out.println("ia is possible? "+g.infiniteArbitragePossible(3));
        //System.out.println("ia is possible? "+g.infiniteArbitragePossible(4));
        //System.out.println("ia is possible? "+g.infiniteArbitragePossible(5));


        //PROBLEMS REMAINING:
        //-path to cycle
        //-path from cycle
        //-output

        //SOLUTION:
        //-only tell if Infinite Arbitrage is possible

    }
}



