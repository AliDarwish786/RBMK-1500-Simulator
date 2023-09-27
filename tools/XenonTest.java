public class XenonTest {
    public static void main(String[] args) {
        long x = 1; 
        double i135Count = 0;
        double xe135Count = 0;
        double I135DECAY_MULTIPLIER = Math.pow(0.5, 1.0 / (72000.0 * 6.57));
        double XE135DECAY_MULTIPLIER = Math.pow(0.5, 1.0 / (72000.0 * 9.14));
        double xeThermalUtilizationModifier = 0;
        double neutronPopulation = 4800.0 / 1661.0 / 2.8898254064 * 29986861831.1868724665;       
        while(x < 200) {
            for (int i = 0; i < 3600 * 20; i++) {
                i135Count += 3001050 * neutronPopulation * 0.063 * 0.05;//* 0.05; //generate 0.063 atoms of I135 per fission event
                xe135Count += i135Count * (1 - I135DECAY_MULTIPLIER);
                i135Count *= I135DECAY_MULTIPLIER;
                xe135Count *= XE135DECAY_MULTIPLIER;
                xe135Count -= 2000000e-24 * xe135Count * 1661 * neutronPopulation * 0.05;// * 0.05;
                if (xe135Count < 0) {
                    xe135Count = 0;
                }
                if (i135Count < 0) {
                    i135Count = 0;
                }
            }
            xeThermalUtilizationModifier = 0.876167866 - (3508920.0 / (495929.0 + 3508920.0 + (xe135Count * 1661 * 2000000e-24)));
            System.out.println(x + "h I135: " + i135Count + " Xe135: " + xe135Count + " NCount: " + neutronPopulation);
            x++;
        }
        neutronPopulation = 0;
        while(x < 400) {
            for (int i = 0; i < 3600 * 20; i++) {
                i135Count += 3001050 * neutronPopulation * 0.063 * 0.05;//* 0.05; //generate 0.063 atoms of I135 per fission event
                xe135Count += i135Count * (1 - I135DECAY_MULTIPLIER);
                i135Count *= I135DECAY_MULTIPLIER;
                xe135Count *= XE135DECAY_MULTIPLIER;
                xe135Count -= 2000000e-24 * xe135Count * 1661 * neutronPopulation * 0.05;// * 0.05;
                if (xe135Count < 0) {
                    xe135Count = 0;
                }
                if (i135Count < 0) {
                    i135Count = 0;
                }
            }
            xeThermalUtilizationModifier = 0.876167866 - (3508920.0 / (495929.0 + 3508920.0 + (xe135Count * 1661 * 2000000e-24)));
            System.out.println(x + "h I135: " + i135Count + " Xe135: " + xe135Count + " NCount: " + neutronPopulation);
            x++;
        }
    }
}
