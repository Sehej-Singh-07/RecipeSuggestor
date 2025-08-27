package recipesuggester;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class RecipeSuggester extends JFrame {
    private static final String FOOD_FILE = "foods.csv";
    private static final int GRID_SIZE = 3;
    private static final int NUM_FOODS = GRID_SIZE * GRID_SIZE;

    // Panels
    private CardLayout cardLayout = new CardLayout();
    private JPanel mainPanel = new JPanel(cardLayout);
    private JPanel welcomePanel;
    private JPanel gridPanel;
    private JPanel foodDetailPanel;

    private List<Food> foodList;
    private List<Food> lastSuggestedFoods = new ArrayList<>();

    // Colors and fonts
    private static final Color DARK_GREEN = new Color(20, 45, 30);
    private static final Color BUTTON_GREEN = new Color(36, 155, 84);
    private static final Color BORDER_GREEN = new Color(36, 155, 84);
    private static final Color WHITE = Color.WHITE;
    private static final Font MAIN_FONT = new Font("Segoe Script", Font.BOLD, 21);
    private static final Font BUTTON_FONT = new Font("Arial Rounded MT Bold", Font.BOLD, 18);

    public RecipeSuggester() {
        setTitle("Recipe Suggester");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Load food data
        foodList = loadFoodsFromCSV(FOOD_FILE);

        // Panels
        welcomePanel = createWelcomePanel();
        gridPanel = new JPanel(new GridLayout(GRID_SIZE, GRID_SIZE, 12, 12));
        gridPanel.setBackground(DARK_GREEN);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        foodDetailPanel = new JPanel();
        foodDetailPanel.setBackground(DARK_GREEN);

        mainPanel.add(welcomePanel, "welcome");
        mainPanel.add(new JScrollPane(gridPanel), "grid");
        mainPanel.add(new JScrollPane(foodDetailPanel), "foodDetail");
        add(mainPanel);

        setSize(900, 700);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(DARK_GREEN);

        JLabel title = new JLabel("Welcome to Recipe Suggester!", JLabel.CENTER);
        title.setFont(new Font("Segoe Script", Font.BOLD, 36));
        title.setForeground(WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(50, 0, 20, 0));

        // Use HTML for wrapping, centering, and color
        JEditorPane descPane = new JEditorPane();
        descPane.setEditable(false);
        descPane.setContentType("text/html");
        descPane.setBackground(DARK_GREEN);
        descPane.setText(
            "<html><body style='text-align: center; color: white; font-family: Serif; font-size: 21px;'>"
            + "Discover delicious foods tailored to your taste!<br><br>"
            + "We'll show you foods from around the world—just click your favorite, and we'll suggest more you'll love!<br>"
            + "Explore details about each dish and find your next craving."
            + "</body></html>"
        );
        descPane.setBorder(BorderFactory.createEmptyBorder(0, 120, 0, 120));

        JPanel textPanel = new JPanel();
        textPanel.setBackground(DARK_GREEN);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.add(title);
        textPanel.add(descPane);

        JButton startButton = new JButton("Find Recipes");
        startButton.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 28));
        startButton.setBackground(WHITE);
        startButton.setForeground(BUTTON_GREEN);
        startButton.setFocusPainted(false);
        startButton.setBorder(new LineBorder(BORDER_GREEN, 3, true));
        startButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.addActionListener(e -> {
            displayRandomFoods();
            cardLayout.show(mainPanel, "grid");
        });

        // Center the button horizontally
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(DARK_GREEN);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(startButton);
        buttonPanel.add(Box.createHorizontalGlue());

        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(DARK_GREEN);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(textPanel);
        centerPanel.add(Box.createVerticalStrut(30));
        centerPanel.add(buttonPanel);
        centerPanel.add(Box.createVerticalGlue());

        panel.add(centerPanel, BorderLayout.CENTER);
        return panel;
    }

    private void displayFoods(List<Food> foods) {
        gridPanel.removeAll();
        lastSuggestedFoods = new ArrayList<>(foods);

        for (Food food : foods) {
            JPanel btnPanel = new JPanel();
            btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.Y_AXIS));
            btnPanel.setBackground(WHITE);
            btnPanel.setBorder(new CompoundBorder(
                new LineBorder(BORDER_GREEN, 3, true),
                new EmptyBorder(20, 10, 20, 10)
            ));

            JButton foodBtn = new JButton("<html><div style='text-align:center;'>" + food.name + "</div></html>");
            foodBtn.setFont(MAIN_FONT);
            foodBtn.setBackground(WHITE);
            foodBtn.setForeground(BUTTON_GREEN);
            foodBtn.setFocusPainted(false);
            foodBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            foodBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            foodBtn.addActionListener(e -> onFoodSelected(food));

            JButton learnMoreBtn = new JButton("Learn More");
            learnMoreBtn.setFont(BUTTON_FONT);
            learnMoreBtn.setBackground(WHITE);
            learnMoreBtn.setForeground(BUTTON_GREEN);
            learnMoreBtn.setFocusPainted(false);
            learnMoreBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            learnMoreBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            learnMoreBtn.setBorder(new LineBorder(BORDER_GREEN, 2, true));
            learnMoreBtn.addActionListener(e -> {
                showFoodDetail(food);
            });

            btnPanel.add(foodBtn);
            btnPanel.add(Box.createVerticalStrut(8));
            btnPanel.add(learnMoreBtn);

            gridPanel.add(btnPanel);
        }

        // Quit button at the bottom
        JButton quitBtn = new JButton("Quit");
        quitBtn.setFont(BUTTON_FONT);
        quitBtn.setBackground(WHITE);
        quitBtn.setForeground(BUTTON_GREEN);
        quitBtn.setFocusPainted(false);
        quitBtn.setBorder(new LineBorder(BORDER_GREEN, 2, true));
        quitBtn.addActionListener(e -> System.exit(0));
        JPanel quitPanel = new JPanel();
        quitPanel.setBackground(DARK_GREEN);
        quitPanel.add(quitBtn);

        gridPanel.revalidate();
        gridPanel.repaint();

        // Remove previous quit panel if present, add new one below grid
        if (mainPanel.getComponentCount() > 1)
            mainPanel.remove(1);
        JPanel gridWithQuit = new JPanel(new BorderLayout());
        gridWithQuit.setBackground(DARK_GREEN);
        gridWithQuit.add(gridPanel, BorderLayout.CENTER);
        gridWithQuit.add(quitPanel, BorderLayout.SOUTH);
        mainPanel.add(new JScrollPane(gridWithQuit), "grid");
        cardLayout.show(mainPanel, "grid");
    }

    private void displayRandomFoods() {
        List<Food> randomFoods = new ArrayList<>(foodList);
        Collections.shuffle(randomFoods);
        displayFoods(randomFoods.subList(0, Math.min(NUM_FOODS, randomFoods.size())));
    }

    private void onFoodSelected(Food selectedFood) {
        List<Food> similarFoods = findSimilarFoods(selectedFood);
        final List<Food> displayFoods = new ArrayList<>(similarFoods);

        Set<Food> exclude = new HashSet<>(lastSuggestedFoods);
        exclude.add(selectedFood);

        if (displayFoods.size() < NUM_FOODS) {
            List<Food> remainingFoods = foodList.stream()
                    .filter(f -> !displayFoods.contains(f) && !exclude.contains(f))
                    .collect(Collectors.toList());
            Collections.shuffle(remainingFoods);
            while (displayFoods.size() < NUM_FOODS && !remainingFoods.isEmpty()) {
                displayFoods.add(remainingFoods.remove(0));
            }
        } else if (displayFoods.size() > NUM_FOODS) {
            Collections.shuffle(displayFoods);
            displayFoods.subList(NUM_FOODS, displayFoods.size()).clear();
        }
        displayFoods(displayFoods);
    }

    private void showFoodDetail(Food food) {
        foodDetailPanel.removeAll();
        foodDetailPanel.setBackground(DARK_GREEN);
        foodDetailPanel.setLayout(new BorderLayout());

        JLabel nameLabel = new JLabel(food.name, JLabel.CENTER);
        nameLabel.setFont(new Font("Segoe Script", Font.BOLD, 44));
        nameLabel.setForeground(WHITE);
        nameLabel.setBorder(BorderFactory.createEmptyBorder(30, 10, 12, 10));

        JPanel attrPanel = new JPanel();
        attrPanel.setBackground(DARK_GREEN);
        attrPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        attrPanel.setLayout(new BoxLayout(attrPanel, BoxLayout.Y_AXIS));

        JLabel attrLabel = new JLabel("Attributes:", JLabel.CENTER);
        attrLabel.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 25));
        attrLabel.setForeground(WHITE);
        attrLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        attrPanel.add(attrLabel);

        attrPanel.add(makeAttrLabel("Type: " + food.sweetOrSavory));
        attrPanel.add(makeAttrLabel("Diet: " + food.vegType));
        attrPanel.add(makeAttrLabel("Cuisine: " + food.cuisine));
        attrPanel.add(makeAttrLabel("Ingredients: " + String.join(", ", food.ingredients)));

        JButton backBtn = new JButton("Back");
        backBtn.setFont(BUTTON_FONT);
        backBtn.setBackground(WHITE);
        backBtn.setForeground(BUTTON_GREEN);
        backBtn.setFocusPainted(false);
        backBtn.setBorder(new LineBorder(BORDER_GREEN, 2, true));
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "grid"));

        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(DARK_GREEN);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(attrPanel);
        centerPanel.add(Box.createVerticalGlue());

        foodDetailPanel.add(nameLabel, BorderLayout.NORTH);
        foodDetailPanel.add(centerPanel, BorderLayout.CENTER);
        foodDetailPanel.add(backBtn, BorderLayout.SOUTH);
        foodDetailPanel.revalidate();
        foodDetailPanel.repaint();
        cardLayout.show(mainPanel, "foodDetail");
    }

    private JLabel makeAttrLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Serif", Font.PLAIN, 21));
        label.setForeground(WHITE);
        label.setBorder(new EmptyBorder(8, 8, 8, 8));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private List<Food> findSimilarFoods(Food selectedFood) {
        List<Food> stage1 = foodList.stream()
                .filter(f -> !f.equals(selectedFood))
                .filter(f -> f.sweetOrSavory.equals(selectedFood.sweetOrSavory))
                .filter(f -> f.vegType.equals(selectedFood.vegType))
                .filter(f -> f.cuisine.equals(selectedFood.cuisine))
                .collect(Collectors.toList());

        stage1.sort((a, b) -> Integer.compare(
                countCommonIngredients(b, selectedFood),
                countCommonIngredients(a, selectedFood)
        ));

        if (stage1.size() < 3) {
            List<Food> stage2 = foodList.stream()
                    .filter(f -> !f.equals(selectedFood))
                    .filter(f -> f.sweetOrSavory.equals(selectedFood.sweetOrSavory))
                    .filter(f -> f.vegType.equals(selectedFood.vegType))
                    .collect(Collectors.toList());
            stage2.removeAll(stage1);
            stage2.sort((a, b) -> Integer.compare(
                    countCommonIngredients(b, selectedFood),
                    countCommonIngredients(a, selectedFood)
            ));
            stage1.addAll(stage2);
        }

        if (stage1.size() < 3) {
            List<Food> stage3 = foodList.stream()
                    .filter(f -> !f.equals(selectedFood))
                    .filter(f -> f.sweetOrSavory.equals(selectedFood.sweetOrSavory))
                    .collect(Collectors.toList());
            stage3.removeAll(stage1);
            stage3.sort((a, b) -> Integer.compare(
                    countCommonIngredients(b, selectedFood),
                    countCommonIngredients(a, selectedFood)
            ));
            stage1.addAll(stage3);
        }

        List<Food> unique = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (Food f : stage1) {
            if (!seen.contains(f.name)) {
                unique.add(f);
                seen.add(f.name);
            }
        }
        return unique;
    }

    private int countCommonIngredients(Food a, Food b) {
        Set<String> setA = new HashSet<>(a.ingredients);
        Set<String> setB = new HashSet<>(b.ingredients);
        setA.retainAll(setB);
        return setA.size();
    }

    private List<Food> loadFoodsFromCSV(String filename) {
        List<Food> foods = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                Food f = Food.fromCSV(line);
                if (f != null) foods.add(f);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Could not load food data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        return foods;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RecipeSuggester::new);
    }

    // Food data class
    static class Food {
        String name;
        String sweetOrSavory;
        String vegType;
        String cuisine;
        List<String> ingredients;
        String imageUrl;

        Food(String name, String sweetOrSavory, String vegType, String cuisine, List<String> ingredients, String imageUrl) {
            this.name = name;
            this.sweetOrSavory = sweetOrSavory;
            this.vegType = vegType;
            this.cuisine = cuisine;
            this.ingredients = ingredients;
            this.imageUrl = imageUrl;
        }

        static Food fromCSV(String line) {
            String[] parts = line.split(",");
            if (parts.length < 6) return null;
            String name = parts[0].trim();
            String sweetOrSavory = parts[1].trim();
            String vegType = parts[2].trim();
            String cuisine = parts[3].trim();
            String imageUrl = parts[parts.length-1].trim();
            List<String> ingredients = Arrays.stream(parts, 4, parts.length-1)
                    .map(String::trim)
                    .collect(Collectors.toList());
            return new Food(name, sweetOrSavory, vegType, cuisine, ingredients, imageUrl);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Food)) return false;
            Food other = (Food) o;
            return name.equalsIgnoreCase(other.name);
        }

        @Override
        public int hashCode() {
            return name.toLowerCase().hashCode();
        }
    }
}