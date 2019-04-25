import java.awt.*;
import java.awt.geom.AffineTransform;

public class Main {
    public static void main(String[] args) {
        Model model = new Model();
        Window window = new Window(model);

        Canvas canvas = new Canvas();
        canvas.addMovable(Main.makeRagdoll());

        window.add(canvas, BorderLayout.CENTER);
        window.setCanvas(canvas);

        model.notifyObservers();
    }

    public static Movable makeRagdoll() {
        // Main Body
        Torso torso = new Torso("Torso", 120, 200, 50);
        torso.transform(AffineTransform.getTranslateInstance(600, 150));

        // Head -> Attached to torso
        Rotatable head = new Rotatable("Head", 0, 30, 110,60, 110);
        head.setAttachedToBottom();
        head.transform(AffineTransform.getTranslateInstance(30, -115));
        head.setRestriction(50, 310);
        torso.addChild(head);

        // Left Upper Arm -> Attached to torso
        Rotatable luArm = new Rotatable("Left Upper Arm", 10, 28, 25, 30, 130);
        luArm.transform(AffineTransform.getTranslateInstance(-28, 13));
        torso.addChild(luArm);

        // Left lower arm: attached to LUA
        Rotatable llArm = new Rotatable("Left Lower Arm", -10, 15, 0, 30, 100);
        llArm.transform(AffineTransform.getTranslateInstance(0, 130));
        llArm.setRestriction(135, 360 - 135);
        luArm.addChild(llArm);

        // Left hand: attached to LLA
        Rotatable lHand = new Rotatable("Left Hand", 0, 20, 0, 40, 45);
        lHand.transform(AffineTransform.getTranslateInstance(-3, 100));
        lHand.setRestriction(35, 360 - 35);
        llArm.addChild(lHand);

        // Right upper arm: attached to torso
        Rotatable ruArm = new Rotatable("Right Upper Arm", -10, 3, 25, 30, 130);
        ruArm.transform(AffineTransform.getTranslateInstance(118, 13));
        torso.addChild(ruArm);

        // Right lower arm: attached to RUA
        Rotatable rlArm = new Rotatable("Right Lower Arm", 10, 15, 0, 30, 100);
        rlArm.transform(AffineTransform.getTranslateInstance(0, 130));
        rlArm.setRestriction(135, 360 - 135);
        ruArm.addChild(rlArm);

        // Right hand: attached to LLA
        Rotatable rHand = new Rotatable("Right Hand", 0, 20, 0, 40, 45);
        rHand.transform(AffineTransform.getTranslateInstance(-3, 100));
        rHand.setRestriction(35, 360 - 35);
        rlArm.addChild(rHand);

        // Left upper leg: attached to torso
        Rotatable luLeg = new Rotatable("Left Upper Leg", 0, 15, 0, 30, 160);
        luLeg.transform(AffineTransform.getTranslateInstance(15, 200));
        luLeg.setRestriction(90, 360 - 90);
        torso.addChild(luLeg);

        // Left lower leg: attached to LUG
        Rotatable llLeg = new Rotatable("Left Lower Leg", 0, 15, 0, 30, 110);
        llLeg.transform(AffineTransform.getTranslateInstance(0, 160));
        llLeg.setRestriction(90, 360 - 90);
        luLeg.addChild(llLeg);

        // Left Foot: attached to LLL
        Rotatable lFoot = new Rotatable("Left Foot", 0, 60,5,70,30);
        lFoot.transform(AffineTransform.getTranslateInstance(-50, 105));
        lFoot.setRestriction(35, 360 - 35);
        llLeg.addChild(lFoot);

        // Left is scalable
        llLeg.enableScaling(110, luLeg, lFoot);
        luLeg.enableScaling(160, llLeg, lFoot);

        // Right upper leg: attached to torso
        Rotatable ruLeg = new Rotatable("Right Upper Leg", 0, 15, 0, 30, 160);
        ruLeg.transform(AffineTransform.getTranslateInstance(75, 200));
        ruLeg.setRestriction(90, 360 - 90);
        torso.addChild(ruLeg);

        // Right lower leg: attached to RUG
        Rotatable rlLeg = new Rotatable("Right Lower Leg", 0, 15, 0, 30, 110);
        rlLeg.transform(AffineTransform.getTranslateInstance(0, 160));
        rlLeg.setRestriction(90, 360 - 90);
        ruLeg.addChild(rlLeg);

        // Right Foot: attached to RLL
        Rotatable rFoot = new Rotatable("Right Foot", 0, 10,5,70,30);
        rFoot.transform(AffineTransform.getTranslateInstance(10, 105));
        rFoot.setRestriction(35, 360 - 35);
        rlLeg.addChild(rFoot);

        // Right is scalable
        rlLeg.enableScaling(110, ruLeg, rFoot);
        ruLeg.enableScaling(160, rlLeg, rFoot);

        Movable.enableDebug();

        return torso;
    }
}
