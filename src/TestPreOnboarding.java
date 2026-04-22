import javax.swing.JFrame;
import gui.PreOnboardingPage;

public class TestPreOnboarding {
    public static void main(String[] args) {

        JFrame frame = new JFrame("Pre-Onboarding Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 700);

        frame.setContentPane(new PreOnboardingPage());

        frame.setVisible(true);
    }
}
