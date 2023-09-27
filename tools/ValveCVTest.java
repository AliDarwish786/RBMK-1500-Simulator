public class ValveCVTest {
    public static void main(String[] args) {
        //steamValve(745.76, 6960.0, 1000.0, 36.015, 300.0);    //SDV-C 4 per loop
        //waterValve(1667.52, 1766, 1400, 1001.9, 0.887);        //Dearator for 180 C
        waterValve(827.85, 2885, 1400, 1001.9, 0.887);        //Dearator for 180 C new
        //waterValve(498.01, 8550, 7550, 1254.2, 0.876);        //Main Feeder 3 per loop for 190 C
        //waterValve(197.44, 8400, 7420, 1254.2, 0.876);        //Aux Feeder 1 per loop for 190 C
        //steamValve(5.14, 6960.0, 0.10142, 36.015, 300.0);     //ejector?
        //steamValve(48.81, 6960.0, 1280, 36.015, 1);             //dearator inlet   
        //steamValve(267.38, 1280, 0.10142, 6.525, 1);             //dearator outlet   
        //steamValve(471.03, 7060, 1000.0, 36.015, 300.0);    //SDV-A 1 per loop
        //steamValve(461.33, 7360, 1000.0, 36.015, 300.0);    //msv group 1
        //steamValve(458.53, 7450, 1000.0, 36.015, 300.0);    //msv group 2
        //steamValve(455.48, 7550, 1000.0, 36.015, 300.0);    //msv group 3
        //waterValve(209.77, 2157, 1400, 42.45, 0.997); //dearator makeup valve for 30 C
        //waterValve(41.4, 1000, 101.4, 42.45, 0.997);
    }

    static void waterValve(double cV, double p1, double p2, double pVapor, double sG) { // pressures in Kpa //flows in m3/h
        final double FL = 0.9, FF = getCriticalPressureRatioFactor(pVapor * 1000, 22120000.0);
        double flow = 0.0;
        if (p1 - p2 < Math.pow(FL, 2) * (p1 - FF * pVapor)) {
            flow = 0.0865 * cV * Math.sqrt((p1 - p2) / sG);
        } else {
            flow = 0.0865 * cV * FL  * Math.sqrt((p1 - FF * pVapor) / sG);
        }
        System.out.println("flow based on cv " + cV + ": " + flow);

        //flow = 571.23; //aux feeder
        //flow = 2930.0; //dearator
        //flow = 500; //dearator makeup valve
        //flow = 100; //dearator overflow valve

        if (p1 - p2 < Math.pow(FL, 2) * (p1 - FF * pVapor)) {
            cV = flow / 0.0865 * Math.sqrt(sG / (p1 - p2));
        } else {
            cV = flow / (0.0865 * FL) * Math.sqrt(sG / (p1 - FF * pVapor));
        }
        System.out.println("cV based on flow " + flow + ": " + cV);
    }

    static double getCriticalPressureRatioFactor(double pSat, double pCritical) { //arguments to be given in pa
        return 0.96 - 0.28 * Math.sqrt(pSat / pCritical);
    }


    static void steamValve(double cV, double p1, double p2, double p, double diameter) { //pressures in Kpa //flows in kg/h
        final double fY = 1.3 / 1.4;
        double pC = (p1 - p2) / p1; 
        double flow = 0.0;
        final double X = 0.72;//1.06235;
        final double C = 0.147235;


        if (pC < fY * X) {
            flow = 2.73 * C * Math.pow(diameter / 4.654, 2) * (1 - pC / (3 * fY * X)) * Math.sqrt((p1 - p2) * p);
        } else {
            flow = 0.66 * 2.73 * C * Math.pow(diameter / 4.654, 2) * Math.sqrt(fY * X * p1 * p);
        }     
        System.out.println("Flow based on orifice calculation: " + flow);
        System.out.println("Orifice cv: " + C * Math.pow(diameter / 4.654, 2));

        if (pC < fY * X) {
            flow = 2.73 * cV * (1 - (pC / (3 * fY * X))) * Math.sqrt((p1 - p2) * p);
        } else {
            flow = 0.66 * 2.73 * cV * Math.sqrt(fY * X * p1 * p);
        }
        System.out.println("flow based on cv " + cV + ": " + flow);

        //flow = 550080.0;  //sdv-c
        //flow = 1.055 * 3600; //ejector
        //flow = 50 * 3600; //dearator inlet and outlet
        //flow = 349920; // sdv-a, msv(all)


        if (pC < fY * X) {
            cV = flow / ((2.73 * (1 - pC / (3 * fY * X)) * Math.sqrt((p1 - p2) * p)));
        } else {
            cV = flow / (0.66 * 2.73 * Math.sqrt(fY * X * p1 * p));
        }
        System.out.println("cv based on flow " + flow + ": " + cV);


        final double fX = 1.3 / 0.72;

        if (pC <  fX) {
            flow = 2.73 * cV * (1 - (pC / (3 * 1.3 * 0.72))) * Math.sqrt((p1 - p2) * p);
        } else {
            flow = 0.66 * 2.73 * cV * Math.sqrt(0.72 * 1.3 * p1 * p);
        }
        if (Double.isNaN(flow)) {
            flow = 0.0;
        } 
        System.out.println("flow from alternative calculation method: " + flow);
    }
}
