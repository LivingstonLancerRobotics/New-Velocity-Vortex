package org.firstinspires.ftc.teamcode.DriversAndHardware;

import com.kauailabs.navx.ftc.AHRS;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/**
 * Created by david.lin on 2/16/2017.
 */

public class Hardware3415WithProtection {

    /* Public OpMode members. */
    public DcMotor bl = null;
    public DcMotor fr = null;
    public DcMotor fl = null;
    public DcMotor br = null;
    public DcMotor collector = null;
    public DcMotor liftLeft = null;
    public DcMotor liftRight = null;
    public DcMotor piston = null;
    public Servo beaconPushLeft = null, beaconPushRight = null, clampLeft = null, clampRight = null, rollerRelease = null;
    public AHRS navx_device = null;
    public ColorSensor colorSensor = null;
    //public ColorSensor lineTrackerF = null;
    //public ColorSensor lineTrackerB = null;
    //
    public OpticalDistanceSensor ods = null;
    public boolean beaconBlue;

    public static final double LEFT_BEACON_INITIAL_STATE = 156.0 / 255;
    public static final double LEFT_BEACON_PUSH = 1.0 / 255;
    public static final double RIGHT_BEACON_PUSH= 155.0 / 255;
    public static final double RIGHT_BEACON_INITIAL_STATE = 0.0;
    public static final double LEFT_CLAMP_INITIAL_STATE = 1;
    public static final double LEFT_CLAMP_UP = 0;
    public static final double LEFT_CLAMP_CLAMP = 70.0 / 255;
    public static final double RIGHT_CLAMP_INITIAL_STATE = 0;
    public static final double RIGHT_CLAMP_UP = 1;
    public static final double RIGHT_CLAMP_CLAMP = 180.0 / 255;
    public static final double ROLLER_RELEASE_IN = 245.0 / 255;
    public static final double ROLLER_RELEASE_OUT = 0.0;


    //Motor, Servo, and Sensor Names
    public static final String servo = "servo";
    public static final String beaconPushLeftName = "beacon_left";
    public static final String beaconPushRightName = "beacon_right";
    public static final String clampLeftName = "clamp_left";
    public static final String clampRightName = "clamp_right";
    public static final String rollerReleaseName = "roller_release";
    public static final String frName = "front_right";
    public static final String flName = "front_left";
    public static final String brName = "back_right";
    public static final String blName = "back_left";
    public static final String liftLeftName = "lift_left";
    public static final String liftRightName = "lift_right";
    public static final String pistonName = "piston";
    public static final String collectorName = "collector";
    public static final String colorSensorName = "color";
    //public static final String lineTrackerFName = "lineTrackerF";
    //public static final String lineTrackerBName = "lineTrackerB";
    public static final String odsName = "ods";

    /* Other Important Data */
    public static final int NAVX_DIM_I2C_PORT = 0;
    public static final byte NAVX_DEVICE_UPDATE_RATE_HZ = 50;
    public static final double HEADING_THRESHOLD = 1.2;      // As tight as we can make it with an integer gyro
    public static final double P_TURN_COEFF = 0.1;     // Larger is more responsive, but also less stable
    public static final double P_DRIVE_COEFF = 0.15;
    public static final double MAX_MOTOR_SPEED = 0.86;
    public static final double WHEEL_DIAMETER = 3.93701;
    public static final String cdim = "dim";

    /* Local OpMode members. */
    HardwareMap hwMap = null;
    private ElapsedTime period = new ElapsedTime();

    /*Toggle Stuff*/
    public static boolean beaconPushLeftButtonPressed = false;
    public static double[] beaconPushLeftPositions = {LEFT_BEACON_INITIAL_STATE, LEFT_BEACON_PUSH};
    public static int beaconPushLeftPos;
    public static int beaconPushLeftToggleReturnArray[] = new int[2];
    public static boolean beaconPushRightButtonPressed = false;
    public static double[] beaconPushRightPositions = {RIGHT_BEACON_INITIAL_STATE, RIGHT_BEACON_PUSH};
    public static int beaconPushRightPos;
    public static int beaconPushRightToggleReturnArray[] = new int[2];

    /* Initialize standard Hardware interfaces */
    public void init(HardwareMap ahwMap, boolean autonomous) {
        // save reference to HW Map
        hwMap = ahwMap;

        // Define and Initialize Motors
        fl = hwMap.dcMotor.get(flName);
        fr = hwMap.dcMotor.get(frName);
        bl = hwMap.dcMotor.get(blName);
        br = hwMap.dcMotor.get(brName);
        collector = hwMap.dcMotor.get(collectorName);
        piston = hwMap.dcMotor.get(pistonName);
        liftRight = hwMap.dcMotor.get(liftRightName);
        liftLeft = hwMap.dcMotor.get(liftLeftName);
        if (autonomous) {
            fl.setDirection(DcMotor.Direction.REVERSE);
            bl.setDirection(DcMotor.Direction.REVERSE);
        }
        liftLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        liftRight.setDirection(DcMotorSimple.Direction.REVERSE);


        piston.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        collector.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Set all motors to zero power
        restAllMotors();

        // Set all motors to run without encoders.
        // May want to use RUN_USING_ENCODERS if encoders are installed.
        fl.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        fr.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        bl.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        br.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        collector.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        piston.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        liftRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        liftLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);


        // Define and initialize ALL installed servos.
        beaconPushLeft = hwMap.servo.get(beaconPushLeftName);
        beaconPushRight = hwMap.servo.get(beaconPushRightName);
        rollerRelease = hwMap.servo.get(rollerReleaseName);
        clampLeft = hwMap.servo.get(clampLeftName);
        clampRight = hwMap.servo.get(clampRightName);
        if (autonomous) {
            beaconPushLeft.setPosition(LEFT_BEACON_PUSH);
            beaconPushRight.setPosition(RIGHT_BEACON_PUSH);
            clampLeft.setPosition(LEFT_CLAMP_INITIAL_STATE);
            clampRight.setPosition(RIGHT_CLAMP_INITIAL_STATE);
            rollerRelease.setPosition(ROLLER_RELEASE_IN);
            fl.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            fr.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            bl.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            br.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            //Define all sensors
            colorSensor = hwMap.colorSensor.get(colorSensorName);
            colorSensor.enableLed(false);
            //lineTrackerF = hwMap.colorSensor.get(lineTrackerFName);
            //lineTrackerB = hwMap.colorSensor.get(lineTrackerBName);
            ods = hwMap.opticalDistanceSensor.get(odsName);
            fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            changeDriveMode(DcMotor.RunMode.RUN_USING_ENCODER);
        } else {

            beaconPushLeftPos = 1;
            beaconPushLeft.setPosition(beaconPushLeftPositions[0]);
            beaconPushRightPos = 1;
            beaconPushRight.setPosition(beaconPushRightPositions[0]);
            clampLeft.setPosition(LEFT_CLAMP_INITIAL_STATE);
            clampRight.setPosition(RIGHT_CLAMP_INITIAL_STATE);
            rollerRelease.setPosition(ROLLER_RELEASE_IN);
        }
    }

    /***
     * waitForTick implements a periodic delay. However, this acts like a metronome with a regular
     * periodic tick.  This is used to compensate for varying processing times for each cycle.
     * The function looks at the elapsed cycle time, and sleeps for the remaining time interval.
     *
     * @param periodMs Length of wait cycle in mSec.
     */
    public void waitForTick(long periodMs) {

        long remaining = periodMs - (long) period.milliseconds();

        // sleep for the remaining portion of the regular cycle period.
        if (remaining > 0) {
            try {
                Thread.sleep(remaining);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Reset the cycle clock for the next pass.
        period.reset();
    }

    public static float convertYaw(double yaw) {
        if (yaw <= 0) {
            yaw = 360 + yaw; //if yaw is negative, make it positive (makes the turn easier to visualize)
        }
        return (float) yaw;
    }

    public void restAllMotors() {
        fl.setPower(0);
        fr.setPower(0);
        bl.setPower(0);
        br.setPower(0);
        collector.setPower(0);
        liftLeft.setPower(0);
        liftRight.setPower(0);
        piston.setPower(0);
    }

    public int[] servoToggle(boolean button, Servo servo, double[] positions, int currentPos, boolean pressed) {
        //Creates a variable saying the number of servo positions
        int servoPositions = positions.length;

        //Checks to see if a button is pressed
        if (button) {
            pressed = true;
        }

        //If the button is pressed, the servo is set to the value following the previous servo value in the values array.
        //The method also t ells us what is the current position (1, 2, or 3) of the servo and will say if the button is no longer pressed
        if (pressed) {
            if (servoPositions == 2) {
                if (currentPos == 1) {
                    servo.setPosition(positions[1]);
                    if (!button) {
                        pressed = false;
                        currentPos = 2;
                    }
                } else if (currentPos == 2) {
                    servo.setPosition(positions[0]);
                    if (!button) {
                        pressed = false;
                        currentPos = 1;
                    }
                }
            } else if (servoPositions == 3) {
                if (currentPos == 1) {
                    servo.setPosition(positions[1]);
                    if (!button) {
                        pressed = false;
                        currentPos = 2;
                    }
                } else if (currentPos == 2) {
                    servo.setPosition(positions[2]);
                    if (!button) {
                        pressed = false;
                        currentPos = 3;
                    }
                } else if (currentPos == 3) {
                    servo.setPosition(positions[0]);
                    if (!button) {
                        pressed = false;
                        currentPos = 1;
                    }
                }
            }
        }

        //Returns values for toggle return arrays
        int boolPressed;
        if (pressed) {
            boolPressed = 1;
        } else {
            boolPressed = 0;
        }
        int returnArray[] = new int[2];
        returnArray[0] = currentPos;
        returnArray[1] = boolPressed;
        return returnArray;
    }

    public void lift(double power) {
        liftLeft.setPower(power);
        liftRight.setPower(power);
    }

    //Method to run piston motor at the same power
    public void shoot(double power) {
        piston.setPower(power);
    }

    public void changeDriveMode(DcMotor.RunMode mode) {
        fl.setMode(mode);
        fr.setMode(mode);
        bl.setMode(mode);
        br.setMode(mode);
    }

    public void setDriveTarget(int targetTick) {
        br.setTargetPosition(targetTick);
        bl.setTargetPosition(targetTick);
        fl.setTargetPosition(targetTick);
        fr.setTargetPosition(targetTick);
    }

    public void setDrivePower(double power) {
        fr.setPower(power);
        fl.setPower(power);
        br.setPower(power);
        bl.setPower(power);

    }

    public boolean motorsBusy(){
        if(fl.isBusy() && fr.isBusy() && bl.isBusy() && br.isBusy())
            return true;
        return false;
    }

    public boolean motorsReset() {
        if (fr.getCurrentPosition() == 0 && bl.getCurrentPosition() == 0 && fl.getCurrentPosition() == 0 && br.getCurrentPosition() == 0)
            return true;
        return false;
    }

    public boolean motorsTarget(int targetTick) {
        if ((fl.getCurrentPosition() < (targetTick - 50) || bl.getCurrentPosition() < (targetTick - 50) || br.getCurrentPosition() < (targetTick - 50) || fr.getCurrentPosition() < (targetTick - 50)))
            return true;
        return false;
    }

    public void moveStraight1(int inches, boolean backwards, LinearOpMode opMode) {
        fr.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        fr.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        int targetTick = (int) (inches * 1140.0 / (4.0 * Math.PI * 2.0));
        if (!backwards) {

            if (motorsReset()) {
                fr.setTargetPosition(targetTick);
                fr.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            }
            while (!motorsTarget(targetTick) && opMode.opModeIsActive() && !opMode.isStopRequested()) {
                setDrivePower(.5);
                opMode.telemetry.addData("Encoders Reset?", motorsReset());
                opMode.telemetry.addData("Current tick values", fl.getCurrentPosition());
                opMode.telemetry.addData("Current tick values", br.getCurrentPosition());
                opMode.telemetry.update();
                waitForTick(40);
            }
            setDrivePower(0.0);
            fr.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        } else {
            if (motorsReset()) {
                setDriveTarget(targetTick);
                changeDriveMode(DcMotor.RunMode.RUN_TO_POSITION);
            }
            while (!motorsTarget(targetTick) && opMode.opModeIsActive() && !opMode.isStopRequested()) {
                setDrivePower(.5);
                waitForTick(40);
            }
            rest();
            changeDriveMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }

    }

    public void newMoveStraight(int inches, boolean backwards, LinearOpMode opMode) {
        changeDriveMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        changeDriveMode(DcMotor.RunMode.RUN_USING_ENCODER);
        changeDriveMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        int targetTick = (int) (inches * 1140.0 / (4.0 * Math.PI * 2.0));
        if (!backwards) {

            if (motorsReset()) {
                setDriveTarget(targetTick);
                changeDriveMode(DcMotor.RunMode.RUN_TO_POSITION);
            }
            while (!motorsTarget(targetTick) && opMode.opModeIsActive() && !opMode.isStopRequested()) {
                setDrivePower(coast(targetTick, smallest(fl.getCurrentPosition(), bl.getCurrentPosition(), fr.getCurrentPosition(), br.getCurrentPosition())));
                opMode.telemetry.addData("Encoders Reset?", motorsReset());
                opMode.telemetry.addData("Current tick values", fl.getCurrentPosition());
                opMode.telemetry.addData("Current tick values", br.getCurrentPosition());
                opMode.telemetry.update();
                waitForTick(40);
            }
            rest();
            changeDriveMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        } else {
            if (motorsReset()) {
                setDriveTarget(targetTick);
                changeDriveMode(DcMotor.RunMode.RUN_TO_POSITION);
            }
            while ((!motorsTarget(targetTick) && opMode.opModeIsActive() && !opMode.isStopRequested())) {
                setDrivePower(-coast(targetTick, smallest(fl.getCurrentPosition(), bl.getCurrentPosition(), fr.getCurrentPosition(), br.getCurrentPosition())));
                waitForTick(40);
            }
            rest();
            changeDriveMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }

    }

    public void moveStraightWithOr(int inches, boolean backwards, LinearOpMode opMode) {
        opMode.telemetry.addLine("Set to Run Using Encoder");
        opMode.telemetry.update();
        changeDriveMode(DcMotor.RunMode.RUN_USING_ENCODER);
        opMode.telemetry.addLine("Set to Stop And Reset Encoder");
        opMode.telemetry.update();
        changeDriveMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        int targetTick = (int) (inches * 1140.0 / (4.0 * Math.PI * 2.0));
        if (!backwards) {
            while (opMode.opModeIsActive() && !opMode.isStopRequested() && !motorsReset()) {
                opMode.telemetry.addLine("Waiting For Motors To Reset");
                opMode.telemetry.update();
            }
            opMode.telemetry.addLine("Setting Target");
            opMode.telemetry.update();
            setDriveTarget(targetTick);
            opMode.telemetry.addLine("Changing Motor Mode To Run To Position");
            opMode.telemetry.update();
            changeDriveMode(DcMotor.RunMode.RUN_TO_POSITION);
            while (!motorsTarget(targetTick) && opMode.opModeIsActive() && !opMode.isStopRequested()) {
                setDrivePower(coast(targetTick, smallest(fl.getCurrentPosition(), bl.getCurrentPosition(), fr.getCurrentPosition(), br.getCurrentPosition())));
                opMode.telemetry.addData("Encoders Reset?", motorsReset());
                opMode.telemetry.addData("Current tick values", fl.getCurrentPosition());
                opMode.telemetry.addData("Current tick values", br.getCurrentPosition());
                opMode.telemetry.update();
                waitForTick(40);
            }
            rest();
            changeDriveMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        } else {
            while (opMode.opModeIsActive() && !opMode.isStopRequested() && !motorsReset()) {
                opMode.telemetry.addLine("Waiting For Motors To Reset");
                opMode.telemetry.update();
            }
            opMode.telemetry.addLine("Setting Target");
            opMode.telemetry.update();
            setDriveTarget(targetTick);
            opMode.telemetry.addLine("Changing Motor Mode To Run To Position");
            opMode.telemetry.update();
            changeDriveMode(DcMotor.RunMode.RUN_TO_POSITION);
            while (!motorsTarget(targetTick) && opMode.opModeIsActive() && !opMode.isStopRequested()) {
                setDrivePower(-coast(targetTick, smallest(fl.getCurrentPosition(), bl.getCurrentPosition(), fr.getCurrentPosition(), br.getCurrentPosition())));
                waitForTick(40);
            }
            rest();
            changeDriveMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }
        changeDriveMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        restAllMotors();
        changeDriveMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void moveStraight(int inches, boolean backwards, LinearOpMode opMode) {
        changeDriveMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        changeDriveMode(DcMotor.RunMode.RUN_USING_ENCODER);
        changeDriveMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        int targetTick = (int) (inches * 1140.0 / (4.0 * Math.PI * 2.0));
        if (!backwards) {

            if (motorsReset()) {
                setDriveTarget(targetTick);
                changeDriveMode(DcMotor.RunMode.RUN_TO_POSITION);
                while (!motorsTarget(targetTick) && opMode.opModeIsActive() && !opMode.isStopRequested()) {
                    //setDrivePower(coast(targetTick, smallest(fl.getCurrentPosition(), bl.getCurrentPosition(), fr.getCurrentPosition(), br.getCurrentPosition())));
                    setDrivePower(.3);
                    opMode.telemetry.addData("Encoders Reset?", motorsReset());
                    opMode.telemetry.addData("Current tick values", fl.getCurrentPosition());
                    opMode.telemetry.addData("Current tick values", br.getCurrentPosition());
                    opMode.telemetry.update();
                    waitForTick(40);
                }
            }
            changeDriveMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            changeDriveMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

            rest();
        } else {
            if (motorsReset()) {
                setDriveTarget(targetTick);
                changeDriveMode(DcMotor.RunMode.RUN_TO_POSITION);
                while ((fl.getCurrentPosition() < (fl.getTargetPosition() - 50) || bl.getCurrentPosition() < (bl.getTargetPosition() - 50) || br.getCurrentPosition() < (br.getTargetPosition() - 50) || fr.getCurrentPosition() < (fr.getTargetPosition() - 50)) && opMode.opModeIsActive() && !opMode.isStopRequested()) {
                    //setDrivePower(-coast(targetTick, smallest(fl.getCurrentPosition(), bl.getCurrentPosition(), fr.getCurrentPosition(), br.getCurrentPosition())));
                    setDrivePower(-.3);
                    waitForTick(40);
                }
            }
            changeDriveMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            changeDriveMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

            rest();
        }
    }


    public int smallest(int a, int b, int c, int d) {
        int smallest;
        if (Math.abs(a) > Math.abs(b)) {
            smallest = Math.abs(b);
        } else {
            smallest = Math.abs(a);
        }
        if (smallest > Math.abs(c)) {
            smallest = Math.abs(c);
        }
        if (smallest > Math.abs(d)) {
            smallest = Math.abs(d);
        }
        return smallest;
    }

    public static double coast(int target, int currentPosition) {
        target = Math.abs(target);
        currentPosition = Math.abs(currentPosition);
        double power = (currentPosition * 1.0) / target;
        power = .9 / (1 + Math.pow(2.7182, 1.65 * power));
        return power;
    }

    public void timeMove(int seconds, boolean backwards, LinearOpMode opMode) {
        if (!backwards) {
            fr.setPower(.4);
            fl.setPower(.4);
            br.setPower(.4);
            bl.setPower(.4);
            opMode.sleep(seconds * 1000);
        } else {
            fr.setPower(-.4);
            fl.setPower(-.4);
            br.setPower(-.4);
            bl.setPower(-.4);
            opMode.sleep(seconds * 1000);
        }
    }

    //Stops all motors on the drive train
    public void rest() {
        setDrivePower(0);
        setDrivePower(0);
    }

    //Sets the DIRECTION the robot is going, based on the error, for gyro turn
    public double getSteer(double error, double speed) {
        int powerMultiplier = 1;
        if (error < 0) {
            powerMultiplier = -1;
        }
        return Range.clip(powerMultiplier * speed, -1, 1);
    }

    //Gives the DIFFERENCE between current and target angle->as robotError
    public double getError(double targetAngle, LinearOpMode opMode) {

        double robotError;

        // calculate error in -179 to +180 range  (
        robotError = targetAngle - navx_device.getYaw();
        while (robotError > 180 && opMode.opModeIsActive() && !opMode.isStopRequested()) robotError -= 360;
        waitForTick(40);
        while (robotError <= -180 && opMode.opModeIsActive() && !opMode.isStopRequested()) robotError += 360;
        waitForTick(40);
        return robotError;
    }

    //Method that tells the motors the speeds they need to turn.
    public void turn(double power) {
        fr.setPower(-power);
        br.setPower(-power);
        fl.setPower(power);
        bl.setPower(power);
    }

    //Method that has the actual robot turn
    public boolean onHeading(double speed, double angle, double PCoeff, LinearOpMode opMode) {

        //Creates the variables needed for the method
        double error;
        double steer;
        boolean onTarget = false;
        double leftSpeed;
        double rightSpeed;

        //Determine turn power based on how far off the robot is from the correct angle
        error = getError(angle, opMode);

        //Tells the robot to move according to the angle of the robot
        if (Math.abs(error) <= HEADING_THRESHOLD) { //Allows the bot to reach an angle within the range of heading_threshold
            rest(); // stops all motors if navx returns a heading that is within
            steer = 0.0;
            leftSpeed = 0.0;
            rightSpeed = 0.0;
            onTarget = true;
        } else { // will try and get back to the desired angle if it is not within the range of desired angles
            steer = getSteer(error, speed); //calls the method to adjust the angle to the desired angle
            rightSpeed = steer;
            leftSpeed = -rightSpeed;
        }

        // Send desired speeds to motors.
        if (opMode.opModeIsActive() && !opMode.isStopRequested()) turn(rightSpeed);

        // Display information for the driver.
        opMode.telemetry.addData("Target", "%5.2f", angle);
        opMode.telemetry.addData("Err/St", "%5.2f/%5.2f", error, steer);
        opMode.telemetry.addData("Speed.", "%5.2f:%5.2f", leftSpeed, rightSpeed);
        opMode.telemetry.addData("Yaw", navx_device.getYaw());
        return onTarget;
    }

    // Method that is called to turn the robot goes from -180 to 180 degrees
    public void gyroAngle(double angle, double speed, LinearOpMode opMode) {
        //Zero's the gyro value
        changeDriveMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        navx_device.zeroYaw();
        //Turns the robot
        if (opMode.opModeIsActive() && !opMode.isStopRequested()) {
            // keep looping while we are still active, and not on heading.
            while (opMode.opModeIsActive() && !opMode.isStopRequested() && !onHeading(speed, angle, P_TURN_COEFF, opMode)) {
                // Update telemetry & Allow time for other processes to run.
                opMode.telemetry.update();
                waitForTick(40);
            }
        }
        //Brakes all motors
        if (opMode.opModeIsActive() && !opMode.isStopRequested()) rest();
        changeDriveMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void restAndSleep(LinearOpMode opMode) {
        if (opMode.opModeIsActive() && !opMode.isStopRequested()) rest();
        opMode.sleep(100);
        opMode.telemetry.update();
    }

    //Takes in the gyro values and converts to degrees from 0-360
    public float getYaw() {
        float yaw = convertYaw(navx_device.getYaw());
        return yaw;
    }
    public double getLight(LinearOpMode opMode){
        double odsReading = ods.getLightDetected();
        opMode.telemetry.addData("odsReading", "%5.2f", odsReading);
        opMode.telemetry.update();
        return odsReading;
    }

    public int[] getRGB() {
        int red = colorSensor.red(); // store the values the color sensor returns
        int blue = colorSensor.blue();
        int green = colorSensor.green();

        int[] rgb = {red, green, blue};
        return rgb;
    }


    /* public int[] getRGBF(){
         int red = lineTrackerF.red();
         int blue = lineTrackerF.blue();
         int green = lineTrackerF.green();
         int[] rgb ={red, green, blue};
         return rgb;
     }*/
   /* public int[] getRGBB(){
        int red = lineTrackerB.red();
        int blue = lineTrackerB.blue();
        int green = lineTrackerB.green();
        int[] rgb = {red, green, blue};
        return rgb;
*/
    public boolean detectAColor(){
        int rgb[] = getRGB();
        if(rgb[0]> 1 && rgb[2]>1){
            return true;
        }
        return false;
    }
    public boolean BeaconColor()
    {
        int[] rgb = getRGB();
        boolean color = false;
        return color;
    }

}