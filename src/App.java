import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

public class App extends JFrame {
    private JTextField display;
    private StringBuilder currentInput = new StringBuilder();
    private String previousNumber = "";
    private String operator = "";
    private boolean shouldResetDisplay = false;
    
    private static final Map<String, Color> BUTTON_COLORS = new HashMap<>();
    static {
        BUTTON_COLORS.put("Back", new Color(200, 200, 200));
        BUTTON_COLORS.put("CE", new Color(255, 165, 0));
        BUTTON_COLORS.put("C", new Color(220, 80, 80));
        BUTTON_COLORS.put("=", new Color(50, 150, 50));
        BUTTON_COLORS.put("√", new Color(100, 150, 200));
        BUTTON_COLORS.put("operator", new Color(100, 150, 200));
        BUTTON_COLORS.put("number", new Color(220, 220, 220));
    }

    public App() {
        setTitle("Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(400, 550);
        setLocationRelativeTo(null);
        setDefaultLookAndFeelDecorated(true);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(240, 240, 240));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel titlePanel = createTitlePanel();
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        JPanel calculatorPanel = createCalculatorPanel();
        mainPanel.add(calculatorPanel, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);
    }

    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(50, 50, 50));
        titlePanel.setBorder(new LineBorder(new Color(30, 30, 30), 2));

        JLabel titleLabel = new JLabel("CALCULATOR");
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBorder(new EmptyBorder(8, 15, 8, 15));
        titlePanel.add(titleLabel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        buttonPanel.setBackground(new Color(50, 50, 50));

        JButton minimizeBtn = createWindowButton("_", new Color(220, 160, 40));
        minimizeBtn.addActionListener(e -> setState(JFrame.ICONIFIED));

        JButton clearBtn = createWindowButton("□", new Color(220, 160, 40));
        clearBtn.addActionListener(e -> setExtendedState(
            getExtendedState() == JFrame.MAXIMIZED_BOTH ? JFrame.NORMAL : JFrame.MAXIMIZED_BOTH));

        JButton closeBtn = createWindowButton("✕", new Color(220, 80, 80));
        closeBtn.addActionListener(e -> System.exit(0));

        buttonPanel.add(minimizeBtn);
        buttonPanel.add(clearBtn);
        buttonPanel.add(closeBtn);

        titlePanel.add(buttonPanel, BorderLayout.EAST);
        return titlePanel;
    }

    private JButton createWindowButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(30, 25));
        btn.setBackground(bgColor);
        btn.setForeground(Color.BLACK);
        btn.setBorder(new LineBorder(new Color(200, 200, 200), 1));
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setFocusPainted(false);
        addHoverEffect(btn, bgColor, bgColor.brighter());
        return btn;
    }
    
    private void addHoverEffect(JButton btn, Color normal, Color hover) {
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(hover);
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(normal);
            }
        });
    }

    private JPanel createCalculatorPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(240, 240, 240));

        JPanel displayPanel = new JPanel(new BorderLayout());
        displayPanel.setBackground(new Color(60, 60, 60));
        displayPanel.setBorder(new LineBorder(new Color(40, 40, 40), 2));

        display = new JTextField("0");
        display.setFont(new Font("Arial", Font.BOLD, 28));
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setBackground(new Color(60, 60, 60));
        display.setForeground(new Color(0, 255, 0));
        display.setBorder(new EmptyBorder(10, 10, 10, 10));
        display.setEditable(false);
        displayPanel.add(display, BorderLayout.CENTER);

        panel.add(displayPanel, BorderLayout.NORTH);

        JPanel buttonsAndMenuPanel = new JPanel(new BorderLayout(10, 10));
        buttonsAndMenuPanel.setBackground(new Color(240, 240, 240));

        JPanel buttonGridPanel = createButtonsGrid();
        buttonsAndMenuPanel.add(buttonGridPanel, BorderLayout.CENTER);

        JPanel menuPanel = createMenuPanel();
        buttonsAndMenuPanel.add(menuPanel, BorderLayout.SOUTH);

        panel.add(buttonsAndMenuPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createButtonsGrid() {
        JPanel panel = new JPanel(new GridLayout(6, 4, 8, 8));
        panel.setBackground(new Color(240, 240, 240));

        String[][] buttons = {
            {"Back", "CE", "C", "/"},
            {"7", "8", "9", "*"},
            {"4", "5", "6", "-"},
            {"1", "2", "3", "+"},
            {"0", ".", "=", "√"}
        };

        for (String[] row : buttons) {
            for (String label : row) {
                JButton btn = createButton(label);
                panel.add(btn);
            }
        }

        return panel;
    }

    private JButton createButton(String label) {
        JButton btn = new JButton(label);
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setFocusPainted(false);
        btn.setBorder(new LineBorder(new Color(200, 200, 200), 1));
        
        Color bgColor = getColorForButton(label);
        boolean isTextButton = label.equals("Back");
        btn.setBackground(bgColor);
        btn.setForeground(isTextButton ? Color.BLACK : Color.WHITE);
        
        addHoverEffect(btn, bgColor, bgColor.brighter());
        addActionForButton(btn, label);
        
        return btn;
    }
    
    private Color getColorForButton(String label) {
        if (BUTTON_COLORS.containsKey(label)) {
            return BUTTON_COLORS.get(label);
        }
        return "+-*/".contains(label) ? BUTTON_COLORS.get("operator") : BUTTON_COLORS.get("number");
    }
    
    private void addActionForButton(JButton btn, String label) {
        switch (label) {
            case "Back" -> btn.addActionListener(e -> handleBackspace());
            case "CE" -> btn.addActionListener(e -> handleCE());
            case "C" -> btn.addActionListener(e -> handleC());
            case "=" -> btn.addActionListener(e -> handleEquals());
            case "√" -> btn.addActionListener(e -> handleSquareRoot());
            case "+", "-", "*", "/" -> btn.addActionListener(e -> handleOperator(label));
            default -> btn.addActionListener(e -> handleNumber(label));
        }
    }

    private JPanel createMenuPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        panel.setBackground(new Color(240, 240, 240));

        JButton settingsBtn = createMenuButton("⚙ Settings");
        settingsBtn.addActionListener(e -> showSettings());

        JButton aboutBtn = createMenuButton("ℹ About");
        aboutBtn.addActionListener(e -> showAbout());

        JButton helpBtn = createMenuButton("? Help");
        helpBtn.addActionListener(e -> showHelp());

        panel.add(settingsBtn);
        panel.add(aboutBtn);
        panel.add(helpBtn);

        return panel;
    }

    private JButton createMenuButton(String label) {
        JButton btn = new JButton(label);
        btn.setFont(new Font("Arial", Font.PLAIN, 12));
        btn.setBackground(new Color(70, 70, 70));
        btn.setForeground(Color.WHITE);
        btn.setBorder(new LineBorder(new Color(100, 100, 100), 1));
        btn.setFocusPainted(false);
        addHoverEffect(btn, new Color(70, 70, 70), new Color(100, 100, 100));
        return btn;
    }

    private void handleNumber(String num) {
        if (shouldResetDisplay) {
            currentInput = new StringBuilder();
            shouldResetDisplay = false;
        }

        if (num.equals(".") && currentInput.toString().contains(".")) {
            return;
        }

        if (num.equals("0") && currentInput.toString().equals("0")) {
            return;
        }

        currentInput.append(num);
        display.setText(currentInput.toString());
    }

    private void handleOperator(String op) {
        if (currentInput.length() > 0) {
            previousNumber = currentInput.toString();
            operator = op;
            currentInput = new StringBuilder();
            shouldResetDisplay = true;
        }
    }

    private void handleEquals() {
        if (previousNumber.isEmpty() || operator.isEmpty() || currentInput.length() == 0) {
            return;
        }

        double num1 = Double.parseDouble(previousNumber);
        double num2 = Double.parseDouble(currentInput.toString());
        double result = 0;

        switch (operator) {
            case "+":
                result = num1 + num2;
                break;
            case "-":
                result = num1 - num2;
                break;
            case "*":
                result = num1 * num2;
                break;
            case "/":
                if (num2 == 0) {
                    display.setText("Error");
                    currentInput = new StringBuilder();
                    previousNumber = "";
                    operator = "";
                    return;
                }
                result = num1 / num2;
                break;
        }

        String resultStr = (result % 1 == 0) ? String.valueOf((long) result) : String.valueOf(result);
        display.setText(resultStr);
        currentInput = new StringBuilder(resultStr);
        previousNumber = "";
        operator = "";
        shouldResetDisplay = true;
    }

    private void handleSquareRoot() {
        if (currentInput.length() > 0) {
            double num = Double.parseDouble(currentInput.toString());
            double result = Math.sqrt(num);
            String resultStr = (result % 1 == 0) ? String.valueOf((long) result) : String.valueOf(result);
            display.setText(resultStr);
            currentInput = new StringBuilder(resultStr);
            shouldResetDisplay = true;
        }
    }

    private void handleBackspace() {
        if (currentInput.length() > 0) {
            currentInput.deleteCharAt(currentInput.length() - 1);
            display.setText(currentInput.length() == 0 ? "0" : currentInput.toString());
        }
    }

    private void handleCE() {
        currentInput = new StringBuilder();
        display.setText("0");
        shouldResetDisplay = true;
    }

    private void handleC() {
        currentInput = new StringBuilder();
        previousNumber = "";
        operator = "";
        display.setText("0");
        shouldResetDisplay = false;
    }

    private void showSettings() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.add(new JCheckBox("Dark Mode (coming soon)"));
        panel.add(new JCheckBox("Sound Effects (coming soon)"));
        
        JButton closeBtn = new JButton("Close");
        panel.add(closeBtn);
        
        showDialog("Settings", 300, 200, panel, closeBtn);
    }

    private void showAbout() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel label = new JLabel("<html>Calculator v1.0<br>A simple and elegant calculator<br>© 2025</html>");
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(label, BorderLayout.CENTER);
        
        JButton closeBtn = new JButton("Close");
        panel.add(closeBtn, BorderLayout.SOUTH);
        
        showDialog("About", 300, 150, panel, closeBtn);
    }

    private void showHelp() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JTextArea textArea = new JTextArea(
            "HOW TO USE THIS CALCULATOR:\n\n" +
            "• Numbers: Click number buttons (0-9)\n" +
            "• Operators: Click +, -, *, / for operations\n" +
            "• Decimal: Click . for decimal numbers\n" +
            "• Calculate: Click = to get the result\n" +
            "• Clear Entry: CE clears current entry\n" +
            "• Clear All: C clears everything\n" +
            "• Backspace: Click Back to remove last digit\n" +
            "• Square Root: Click √ for square root\n"
        );
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
        
        JButton closeBtn = new JButton("Close");
        panel.add(closeBtn, BorderLayout.SOUTH);
        
        showDialog("Help", 400, 300, panel, closeBtn);
    }
    
    private void showDialog(String title, int width, int height, JPanel panel, JButton closeBtn) {
        JDialog dialog = new JDialog(this, title, true);
        dialog.setSize(width, height);
        dialog.setLocationRelativeTo(this);
        closeBtn.addActionListener(e -> dialog.dispose());
        dialog.add(panel);
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new App());
    }
}
