
package Threads;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;


public class ThreadRunner implements Runnable{

    private String name;
    public static volatile AtomicInteger counter = new AtomicInteger(0);
    
    public ThreadRunner(String name){
        this.name = name;
    }
    
    /*
    @Override
    public void run(){
        while(counter.get() <=50){
            System.out.println(this.name + "  " + counter.getAndIncrement());
        }
        
    }
    */
    @Override
    public void run(){
        int temp = 0;
        while(counter.get() <= 50){
            for(int i = 0; i < 1000000; i++){
                temp++;
            }
            if(counter.get() <= 50)
                System.out.println(this.name + " " + counter.getAndIncrement());
            for(int i = 0; i < 1000000; i++){
                temp--;
            }
        }
    }
    
    public static void main(String[] args){
        for(int i = 0; i < 3; i++){
            ThreadRunner runner = new ThreadRunner("Thread " + i);
            Thread thread = new Thread(runner);
            thread.start();
        }
    }
}
