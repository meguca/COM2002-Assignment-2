package treatments;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import java.sql.*;
//import forms.QueryHandler;

public class TreatmentsTable extends JPanel{

    public TreatmentsTable(JFrame parentF){
        this.parentF = parentF;
        qHand = new QueryHandler("team016", "eabb6f40");
        setLayout(new BorderLayout());       
        initTable(); 
        scrollPane = new JScrollPane(treatTable);
        add(scrollPane);
    }

    public void initTable(){
        treatTable = new JTable(data, colNames);   
    }
    
    public void update(String patID) {
    		String treats = returnTreatmentsForPatient(patID);
        	data  = qHand.executeQueryFull(treats + ";");
        	if (data == null) {
                JOptionPane.showMessageDialog(parentF, "Cannot find patient.");
            }
        	//totalCost
        	JOptionPane.showMessageDialog(parentF, (Float.toString(returnTreatmentsTotalCost(data))));
        	//costDue
        	JOptionPane.showMessageDialog(parentF, (Float.toString(returnTotalCostWithCarePlan(patID, data))));
            //Call call init table with update data[][]
        	initTable();
        	//Repaint window...
           	//call removeAll() to get rid of all comps
            removeAll();
            //create new scroll pane
            scrollPane = new JScrollPane(treatTable);
            //put table in pane
            add(scrollPane);
            //revalidate
            revalidate();
            //repaint
            repaint();
    }
    
	public String returnTreatmentsForPatient(String patId) {
    	String appForPatient = ("(SELECT date, startTime, partner FROM Appointment WHERE patientID = " + patId + ")");
    	String treatForPatient = ("SELECT * FROM Treatment WHERE (date, startTime, partner) IN " + appForPatient);
    	return treatForPatient;
    	//needs a semi-colon added at the end
    }
    
    //returns the total cost of all treatments under the patient
    private float returnTreatmentsTotalCost(Object[][] costs) {
    	float totalCost = 0;
    	for (int x=0;x<costs.length;x++) {
    	     for (int y=0;y<costs[0].length;y++) {
    	    	 String input = costs[x][y].toString();
    	           if (y==4) {
    	        	   //returns total cost
    	        	   totalCost = totalCost + Float.valueOf(input);
    	           }
    	     } 
    	}
    	return totalCost;
    }

	//applies patient's careplan to the total cost and returns a new value
    private float returnTotalCostWithCarePlan(String patID, Object[][] patTreats) {
    	String carePlan = "(SELECT planName FROM Subscription WHERE patientID = " + patID + ");";
		int checkUps = 0;
		int hygeineVisits = 0;
		int repairs = 0;
		int totalCheckUps = 0;
		int totalHygeine = 0;
		int totalRepairs = 0;
		float totalCost = 0;
    	switch (qHand.executeQueryFull(carePlan)[0][0]) {
    		case "NHS free" :
    			totalCheckUps = 2;
    			totalHygeine = 2;
    			totalRepairs = 6;
    			for (int x=0;x<patTreats.length;x++) {
    				String treatName = patTreats[x][0].toString();
    				if ((treatName.equals("check up")) && (checkUps != totalCheckUps)) {
    					checkUps++;
    					patTreats[x][0] = "N/A";
    				}
    				if (treatName.equals("hygeine visit") && (hygeineVisits != totalHygeine)) {
    					hygeineVisits++;
    					patTreats[x][0] = "N/A";
    				}
    				if (treatName.equals("repair") && (repairs != totalRepairs)) {
    					repairs++;
    					patTreats[x][0] = "N/A";
    				}
    	    	}
    			break;
    		case "maintenance" :
    			totalCheckUps = 2;
    			totalHygeine = 2;
    			totalRepairs = 0;
    			for (int x=0;x<patTreats.length;x++) {
    				String treatName = patTreats[x][0].toString();
    				if ((treatName.equals("check up")) && (checkUps != totalCheckUps)) {
    					checkUps++;
    					patTreats[x][0] = "N/A";
    				}
    				if (treatName.equals("hygeine visit") && (hygeineVisits != totalHygeine)) {
    					hygeineVisits++;
    					patTreats[x][0] = "N/A";
    				}
    				if (treatName.equals("repair") && (repairs != totalRepairs)) {
    					repairs++;
    					patTreats[x][0] = "N/A";
    				}
    	    	}
    			break;
    		case "oral health" :
    			totalCheckUps = 2;
    			totalHygeine = 4;
    			totalRepairs = 0;
    			for (int x=0;x<patTreats.length;x++) {
    				String treatName = patTreats[x][0].toString();
    				if ((treatName.equals("check up")) && (checkUps != totalCheckUps)) {
    					checkUps++;
    					patTreats[x][0] = "N/A";
    				}
    				if (treatName.equals("hygeine visit") && (hygeineVisits != totalHygeine)) {
    					hygeineVisits++;
    					patTreats[x][0] = "N/A";
    				}
    				if (treatName.equals("repair") && (repairs != totalRepairs)) {
    					repairs++;
    					patTreats[x][0] = "N/A";
    				}
    	    	}
    			break;
    		case "dental repair" :
    			totalCheckUps = 2;
    			totalHygeine = 2;
    			totalRepairs = 2;
    			for (int x=0;x<patTreats.length;x++) {
    				String treatName = patTreats[x][0].toString();
    				if ((treatName.equals("check up")) && (checkUps != totalCheckUps)) {
    					checkUps++;
    					patTreats[x][0] = "N/A";
    				}
    				if (treatName.equals("hygeine visit") && (hygeineVisits != totalHygeine)) {
    					hygeineVisits++;
    					patTreats[x][0] = "N/A";
    				}
    				if (treatName.equals("repair") && (repairs != totalRepairs)) {
    					repairs++;
    					patTreats[x][0] = "N/A";
    				}
    	    	}
    			break;
    		default : return returnTreatmentsTotalCost(patTreats);
    	}
    	for (int x=0;x<patTreats.length;x++) {
    		if (patTreats[x][0] != "N/A") {
    			String input = patTreats[x][4].toString();
    			totalCost = totalCost + Float.valueOf(input);
   	     	} 
    	}
    	return totalCost;
    }

    String[] colNames = {"Treatment", "Date"};
    Object[][] data = {{"N/A", "N/A"}};

    private JFrame parentF = null;
    private JTable treatTable = null;

    private JScrollPane scrollPane = null;

    private QueryHandler qHand = null;
}