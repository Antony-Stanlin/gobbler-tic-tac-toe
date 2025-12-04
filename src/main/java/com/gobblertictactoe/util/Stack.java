package com.gobblertictactoe.util;

import java.util.Arrays;

public class Stack<T> {

    private int top;
    private int capacity;
    private Object[] stack;

    public Stack(int capacity){
        this.capacity=capacity;
        this.stack=new Object[capacity];
        this.top=-1;
    }

    public Stack(){
        this.capacity=12;
        this.stack=new Object[capacity];
        this.top=-1;
    }

    public void push(T value){
        if(top==capacity-1){
            expandCapacity();
        }
        stack[++top]=value;
    }

   
    public T peek(){
        return (T) stack[top];
    }

    
    public T pop(){
        if(top==-1){
            System.out.println("Stack Underflow");
            return null;
        }
        return (T) stack[top--];
    }

    public int size(){
        return top+1;
    }

    public boolean isEmpty(){
        return top<0;
    }

    private void expandCapacity(){
        int newCapacity=capacity*2;
        stack=Arrays.copyOf(stack, newCapacity);
        capacity=newCapacity;
    }

    @Override
    public String toString(){
        return Arrays.toString(Arrays.copyOfRange(stack, 0, top+1));
    }

    public void clear(){
        top=-1;
    }

    public boolean contains(T value){
        for(int i=0;i<top+1;i++)
            if(value==stack[i])
                return true;
        
        return false;
    }
    
    public Object[] toArray(){
        Object[] arr=Arrays.copyOf(stack, top+1);
        return arr;
    }
}
