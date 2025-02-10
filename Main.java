import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main extends JFrame {
    // ArrayList fields
    private ArrayList<String> inventoryTitles = new ArrayList<>();
    private ArrayList<Double> inventoryPrices = new ArrayList<>();
    private ArrayList<String> cartTitles = new ArrayList<>();
    private ArrayList<Double> cartPrices = new ArrayList<>();

    private double cartSubtotal = 0.0;

    private JList<String> booksListView;
    private JList<String> cartListView;
    private JLabel subtotalOutputLabel;
    private JLabel taxOutputLabel;
    private JLabel totalOutputLabel;

    public Main() {
        // Read book file
        try {
            readBookFile();
        } catch (IOException e) {
            System.out.println("Error reading the file.");
        }

        // Initialize GUI components
        setTitle("Shopping Cart");
        setLayout(new BorderLayout());

        // Create inventory list
        DefaultListModel<String> bookListModel = new DefaultListModel<>();
        for (String title : inventoryTitles) {
            bookListModel.addElement(title);
        }
        booksListView = new JList<>(bookListModel);
        JScrollPane bookScrollPane = new JScrollPane(booksListView);

        // Create cart list
        DefaultListModel<String> cartListModel = new DefaultListModel<>();
        cartListView = new JList<>(cartListModel);
        JScrollPane cartScrollPane = new JScrollPane(cartListView);

        // Create labels for subtotal, tax, and total
        subtotalOutputLabel = new JLabel("0.00");
        taxOutputLabel = new JLabel("0.00");
        totalOutputLabel = new JLabel("0.00");

        // Create buttons
        JButton addToCartButton = new JButton("Add To Cart");
        addToCartButton.addActionListener(new AddToCartListener());
        
        JButton removeFromCartButton = new JButton("Remove From Cart");
        removeFromCartButton.addActionListener(new RemoveFromCartListener());
        
        JButton clearCartButton = new JButton("Clear Cart");
        clearCartButton.addActionListener(new ClearCartListener());
        
        JButton checkoutButton = new JButton("Checkout");
        checkoutButton.addActionListener(new CheckoutListener());

        // Create panels
        JPanel bookPanel = new JPanel(new BorderLayout());
        bookPanel.add(new JLabel("Pick a Book"), BorderLayout.NORTH);
        bookPanel.add(bookScrollPane, BorderLayout.CENTER);
        bookPanel.add(addToCartButton, BorderLayout.SOUTH);

        JPanel cartPanel = new JPanel(new BorderLayout());
        cartPanel.add(new JLabel("Shopping Cart"), BorderLayout.NORTH);
        cartPanel.add(cartScrollPane, BorderLayout.CENTER);
        
        JPanel cartButtonPanel = new JPanel();
        cartButtonPanel.setLayout(new BoxLayout(cartButtonPanel, BoxLayout.Y_AXIS));
        cartButtonPanel.add(removeFromCartButton);
        cartButtonPanel.add(clearCartButton);
        cartButtonPanel.add(checkoutButton);
        
        cartPanel.add(cartButtonPanel, BorderLayout.SOUTH);

        // Create subtotal, tax, and total panel
        JPanel totalsPanel = new JPanel(new GridLayout(3, 2));
        totalsPanel.add(new JLabel("Subtotal:"));
        totalsPanel.add(subtotalOutputLabel);
        totalsPanel.add(new JLabel("Tax:"));
        totalsPanel.add(taxOutputLabel);
        totalsPanel.add(new JLabel("Total:"));
        totalsPanel.add(totalOutputLabel);

        // Add panels to the main frame
        add(bookPanel, BorderLayout.WEST);
        add(cartPanel, BorderLayout.EAST);
        add(totalsPanel, BorderLayout.SOUTH);

        // Set frame properties
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void readBookFile() throws IOException {
        Scanner inFile = new Scanner(new File("BookPrices.txt"));

        while (inFile.hasNext()) {
            String input = inFile.nextLine();
            String[] tokens = input.split(",");
            inventoryTitles.add(tokens[0]);
            inventoryPrices.add(Double.parseDouble(tokens[1]));
        }
        inFile.close();
    }

    private class AddToCartListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int index = booksListView.getSelectedIndex();
            if (index != -1) {
                cartTitles.add(inventoryTitles.get(index));
                cartPrices.add(inventoryPrices.get(index));
                updateCartList();
                cartSubtotal += inventoryPrices.get(index);
                subtotalOutputLabel.setText(String.format("%,.2f", cartSubtotal));
            }
        }
    }

    private class RemoveFromCartListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int index = cartListView.getSelectedIndex();
            if (index != -1) {
                cartSubtotal -= cartPrices.get(index);
                subtotalOutputLabel.setText(String.format("%,.2f", cartSubtotal));
                cartTitles.remove(index);
                cartPrices.remove(index);
                updateCartList();
            }
        }
    }

    private class ClearCartListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            cartSubtotal = 0.0;
            subtotalOutputLabel.setText("0.00");
            cartTitles.clear();
            cartPrices.clear();
            updateCartList();
        }
    }

    private class CheckoutListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            final double TAX_RATE = 0.07;
            double tax = cartSubtotal * TAX_RATE;
            taxOutputLabel.setText(String.format("%,.2f", tax));
            double total = cartSubtotal + tax;
            totalOutputLabel.setText(String.format("%,.2f", total));
        }
    }

    private void updateCartList() {
        DefaultListModel<String> cartListModel = new DefaultListModel<>();
        for (String title : cartTitles) {
            cartListModel.addElement(title);
        }
        cartListView.setModel(cartListModel);
    }

    public static void main(String[] args) {
        new Main();
    }
}