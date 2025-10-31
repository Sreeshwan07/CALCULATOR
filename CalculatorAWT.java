import java.awt.*;
import java.awt.event.*;

public class CalculatorAWT extends Frame implements ActionListener, WindowListener {
    private TextField display;
    private String current = "";
    private String operator = "";
    private double result = 0;
    private boolean startNewNumber = true;
    private String expression = ""; // shows ongoing expression

    public CalculatorAWT() {
        super("Simple AWT Calculator");
        setSize(320, 420);
        setLayout(new BorderLayout(6, 6));

        // --- Display setup ---
        display = new TextField("0");
        display.setEditable(false);
        display.setFont(new Font("SansSerif", Font.BOLD, 22));
        display.setBackground(Color.white);
        display.setForeground(Color.black);
        add(display, BorderLayout.NORTH);

        // --- Buttons layout ---
        Panel buttonPanel = new Panel(new GridLayout(5, 4, 5, 5));
        String[] buttons = {
                "7", "8", "9", "/",
                "4", "5", "6", "*",
                "1", "2", "3", "-",
                "0", ".", "=", "+",
                "C", "%", "<-", ""
        };

        for (String t : buttons) {
            if (t.equals("")) {
                buttonPanel.add(new Label());
            } else {
                Button b = new Button(t);
                b.setFont(new Font("SansSerif", Font.PLAIN, 18));
                b.setBackground(new Color(240, 240, 240));
                b.addActionListener(this);
                buttonPanel.add(b);
            }
        }

        add(buttonPanel, BorderLayout.CENTER);
        addWindowListener(this);
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // --- Button actions ---
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        if ("0123456789".contains(cmd)) {
            enterDigit(cmd);
        } else if (cmd.equals(".")) {
            enterDecimalPoint();
        } else if (cmd.equals("C")) {
            clearAll();
        } else if (cmd.equals("<-")) {
            backspace();
        } else if ("+-*/%".contains(cmd)) {
            applyOperator(cmd);
        } else if (cmd.equals("=")) {
            computeResult();
        }
    }

    private void enterDigit(String d) {
        if (startNewNumber) {
            current = d;
            startNewNumber = false;
        } else {
            if (current.equals("0")) current = d;
            else current += d;
        }
        expression += d;
        display.setText(expression);
    }

    private void enterDecimalPoint() {
        if (startNewNumber) {
            current = "0.";
            startNewNumber = false;
            expression += "0.";
        } else if (!current.contains(".")) {
            current += ".";
            expression += ".";
        }
        display.setText(expression);
    }

    private void clearAll() {
        current = "";
        operator = "";
        result = 0;
        expression = "";
        startNewNumber = true;
        display.setText("0");
    }

    private void backspace() {
        if (expression.length() == 0) {
            display.setText("0");
            return;
        }

        if (expression.endsWith(" ")) { // operator removal
            if (expression.length() >= 3) {
                expression = expression.substring(0, expression.length() - 3);
                operator = "";
                startNewNumber = false;
            }
        } else {
            expression = expression.substring(0, expression.length() - 1);
            if (current.length() > 0) {
                current = current.substring(0, Math.max(0, current.length() - 1));
            } else if (!operator.isEmpty()) {
                operator = "";
            }
        }

        display.setText(expression.isEmpty() ? "0" : expression);
    }

    private void applyOperator(String op) {
        if (!current.isEmpty()) {
            try {
                double temp = Double.parseDouble(current);
                if (operator.isEmpty()) {
                    result = temp;
                } else {
                    result = calculate(result, temp, operator);
                }
            } catch (ArithmeticException ex) {
                showErrorDialog(ex.getMessage());
                return;
            }
        }
        operator = op;
        expression = expression.trim() + " " + op + " ";
        startNewNumber = true;
        display.setText(expression);
    }

    /**
     * THIS IS THE CORRECTED METHOD
     */
    private void computeResult() {
        if (operator.isEmpty() || current.isEmpty()) return;
        try {
            double second = Double.parseDouble(current);
            result = calculate(result, second, operator);
        } catch (ArithmeticException ex) {
            showErrorDialog(ex.getMessage());
            return;
        }

        // Format the number to remove .0 if it's a whole number
        String formattedResult = formatNumber(result);

        // Show only the final formatted result
        display.setText(formattedResult);

        // Reset for the next operation using the CORRECTLY formatted result
        operator = "";
        current = formattedResult;
        expression = formattedResult;
        startNewNumber = true;
    }

    private double calculate(double a, double b, String op) {
        switch (op) {
            case "+": return a + b;
            case "-": return a - b;
            case "*": return a * b;
            case "/":
                if (b == 0) throw new ArithmeticException("Cannot divide by zero");
                return a / b;
            case "%":
                if (b == 0) throw new ArithmeticException("Cannot modulo by zero");
                return a % b;
            default: return b;
        }
    }

    private String formatNumber(double val) {
        if (Double.isNaN(val)) return "Error";
        if (val == (long) val) return String.format("%d", (long) val);
        return String.format("%s", val);
    }

    private void showErrorDialog(String message) {
        Dialog d = new Dialog(this, "Error", true);
        d.setLayout(new FlowLayout());
        Label l = new Label(message);
        Button b = new Button("OK");
        b.addActionListener(ev -> d.dispose());
        d.add(l);
        d.add(b);
        d.setSize(250, 100);
        d.setLocationRelativeTo(this);
        d.setVisible(true);

        // Reset calculator after error
        clearAll();
    }

    // --- WindowListener methods ---
    public void windowClosing(WindowEvent e) { dispose(); System.exit(0); }
    public void windowOpened(WindowEvent e) {}
    public void windowClosed(WindowEvent e) {}
    public void windowIconified(WindowEvent e) {}
    public void windowDeiconified(WindowEvent e) {}
    public void windowActivated(WindowEvent e) {}
    public void windowDeactivated(WindowEvent e) {}

    // --- Main method ---
    public static void main(String[] args) {
        new CalculatorAWT();
    }
}
