
package Threads;

import java.util.concurrent.atomic.AtomicInteger;


public class ThreadRunner implements Runnable{

    private static  AtomicInteger counter = new AtomicInteger(0);       
    
    @Override
    public void run(){
        
        /*
         * verzia 1. nepotrebujeme v poradi
         */
        for(;;){
            int tmp = counter.getAndIncrement();
            if(tmp > 50) break;
            System.out.println(Thread.currentThread().getName() + " " + tmp);            
        }            
    }
    
    
    public static void main(String[] args){
        for(int i = 0; i < 3; i++){
            ThreadRunner runner = new ThreadRunner();
            Thread thread = new Thread(runner,"Thread" + i);
            thread.start();            
        }        
    }  
    
    
        /*
         * verzia 2. cisla vypisovane v poradi
         */
        
        /*private int counter;
        
        @Override
        public void run(){
            synchronized(this){
                for(;;){
                    if(counter > 50) break;
                    System.out.println(counter++);
                }
            }
        }
        
         
        public static void main(String ... args){
           ThreadRunner runner = new ThreadRunner();      
           for(int i = 0; i < 3; i++){
              Thread thread = new Thread(runner,"Thread" + i);
              thread.start();
           }            
        }*/
        
    
}
