package TDA;

public class Stack {
    static final int MAX = 1000;
    int top;
    char a[] = new char[MAX]; // Maximum size of Stack

    public Stack() {
        top = -1;
    }

    public boolean isEmpty() {
        return (top < 0);
    }

    public boolean push(char x) {
        if (top >= (MAX - 1)) {
            return false;
        } else {
            a[++top] = x;
            return true;
        }
    }

    public int pop() {
        if (top < 0) {
            return 0;
        } else {
            char x = a[top--];
            return x;
        }
    }

    int peek() {
        if (top < 0) {
            return 0;
        } else {
            char x = a[top];
            return x;
        }
    }

    public char top() {
        return a[top];
    }
}

