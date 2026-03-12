package week1;
public class question3 {
    static class Node {
        int data;
        Node next;

        Node(int data) {
            this.data = data;
            this.next = null;
        }
    }

    public static Node deleteNode(Node head, int key) {
        if (head == null) {
            return null;
        }

        if (head.data == key) {
            return head.next;
        }

        Node current = head;

        while (current.next != null && current.next.data != key) {
            current = current.next;
        }

        if (current.next != null) {
            current.next = current.next.next;
        }

        return head;
    }

    public static void printList(Node head) {
        Node current = head;
        while (current != null) {
            System.out.print(current.data + " -> ");
            current = current.next;
        }
        System.out.println("null");
    }

    public static void main(String[] args) {
        Node head = new Node(10);
        head.next = new Node(20);
        head.next.next = new Node(30);
        head.next.next.next = new Node(40);

        System.out.println("Original Linked List:");
        printList(head);

        int keyToDelete = 30;
        head = deleteNode(head, keyToDelete);

        System.out.println("\nLinked List after deleting " + keyToDelete + ":");
        printList(head);

        head = deleteNode(head, 10);
        System.out.println("\nLinked List after deleting the head (10):");
        printList(head);
    }
}