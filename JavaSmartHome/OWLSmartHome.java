import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

import javax.swing.plaf.SliderUI;

import org.semanticweb.owlapi.model.AddAxiom;

public class OWLSmartHome
{

	int removeTime = 180;

	boolean printCont = true;
	boolean printSit = true;
	long classtime;

	OWLHelpSmartHome owlHelp;

	boolean SaveLog = false;
	
	private int sleepScale=0;

	ArrayList<String> ListaEventi;

	private int instant;
	int checks = 0;
	int timeclassify = 0;

	int mintime = 65000;
	int maxtime = 0;

	Hashtable<String, Integer> mapEvents = new Hashtable<String, Integer>();

	// SENSORI
	private int numPIR;
	private int numItem;
	private int numDoor;
	private int numStoves;
	private int numFaucets;
	private int numPhones;

	int contaTap = 0;

	private boolean pirs[];
	private boolean items[];
	private boolean doors[];
	private boolean stoves[];
	private boolean faucets[];
	private boolean phones[];

	private boolean prevpirs[];
	private boolean previtems[];
	private boolean prevdoors[];
	private boolean prevstoves[];
	private boolean prevfaucets[];
	private boolean prevphones[];

	private int filterPirs[];
	private int filterItems[];
	private int filterDoors[];
	private int filterStoves[];
	private int filterFaucets[];
	private int filterPhones[];

	private int interPirs[];
	private int interItems[];
	private int interDoors[];
	private int interStoves[];
	private int interFaucets[];
	private int interPhones[];

	/****
	 * HARDCODED ********** private int interPirs[] = { 2, 5, 12, 13, 16, 17,
	 * 19, 50 }; private int interItems[] = { 1, 5 }; private int interDoors[] =
	 * { 9, 10 }; private int interStoves[]; private int interFaucets[] = { 0, 1
	 * }; private int interPhones[] = { 0 };
	 */

	private String sensNames[];

	// Sequenza di eventi
	private ArrayList<String> sequence;

	public OWLSmartHome(OWLHelpSmartHome owle) {

		ListaEventi = new ArrayList<String>();

		sequence = new ArrayList<String>();

		owlHelp = owle;

		instant = 0;
		
		BufferedReader fr = null;
		try
		{
			fr = new BufferedReader(new FileReader(new File("configSH.txt")));
		} catch (FileNotFoundException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String strline="";
		try
		{
			if((strline=fr.readLine()) != null)
				removeTime=Integer.parseInt(strline.replace("removeTime=",""));
			if((strline=fr.readLine()) != null)
				sleepScale=Integer.parseInt(strline.replace("sleepScale=",""));
			if((strline=fr.readLine()) != null)
				numPIR=Integer.parseInt(strline.replace("numPIR=",""));
			if((strline=fr.readLine()) != null)
				numItem=Integer.parseInt(strline.replace("numItem=",""));
			if((strline=fr.readLine()) != null)
				numDoor=Integer.parseInt(strline.replace("numDoor=",""));
			if((strline=fr.readLine()) != null)
				numStoves=Integer.parseInt(strline.replace("numStoves=",""));
			if((strline=fr.readLine()) != null)
				numFaucets=Integer.parseInt(strline.replace("numFaucets=",""));
			if((strline=fr.readLine()) != null)
				numPhones=Integer.parseInt(strline.replace("numPhones=",""));
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*numPIR = 51;
		numItem = 8;
		numDoor = 12;
		numStoves = 1;
		numFaucets = 2;
		numPhones = 1;*/

		// ISTANZA SENSORI
		pirs = new boolean[numPIR];
		items = new boolean[numItem];
		doors = new boolean[numDoor];
		stoves = new boolean[numStoves];
		faucets = new boolean[numFaucets];
		phones = new boolean[numPhones];

		prevpirs = new boolean[numPIR];
		previtems = new boolean[numItem];
		prevdoors = new boolean[numDoor];
		prevstoves = new boolean[numStoves];
		prevfaucets = new boolean[numFaucets];
		prevphones = new boolean[numPhones];

		filterPirs = new int[numPIR];
		filterItems = new int[numItem];
		filterDoors = new int[numDoor];
		filterStoves = new int[numStoves];
		filterFaucets = new int[numFaucets];
		filterPhones = new int[numPhones];

		sensNames = new String[numPIR + numItem + numDoor + numStoves + numFaucets + numPhones];

		int tot = 0;

		for (int i = 0; i < numPIR; i++, tot++)
		{
			pirs[i] = false;
			sensNames[tot] = "iPir" + String.valueOf(i + 1);
		}
		for (int i = 0; i < numItem; i++, tot++)
		{
			items[i] = false;
			sensNames[tot] = "iItem" + String.valueOf(i + 1);
		}
		for (int i = 0; i < numDoor; i++, tot++)
		{
			doors[i] = false;
			sensNames[tot] = "iDoor" + String.valueOf(i + 1);
		}
		for (int i = 0; i < numStoves; i++, tot++)
		{
			stoves[i] = false;
			sensNames[tot] = "iStove" + String.valueOf(i + 1);
		}
		for (int i = 0; i < numFaucets; i++, tot++)
		{
			faucets[i] = false;
			sensNames[tot] = "iFaucet" + String.valueOf(i + 1);
		}
		for (int i = 0; i < numPhones; i++, tot++)
		{
			phones[i] = false;
			sensNames[tot] = "iPhone" + String.valueOf(i + 1);
		}

		for (int i = 0; i < numPIR; i++, tot++)
			filterPirs[i] = -1;
		for (int i = 0; i < numItem; i++, tot++)
			filterItems[i] = -1;
		for (int i = 0; i < numDoor; i++, tot++)
			filterDoors[i] = -1;
		for (int i = 0; i < numStoves; i++, tot++)
			filterStoves[i] = -1;
		for (int i = 0; i < numFaucets; i++, tot++)
			filterFaucets[i] = -1;
		for (int i = 0; i < numPhones; i++, tot++)
			filterPhones[i] = -1;

		/***
		 * HARDCODED ****
		 * 
		 * filterPhones[0] = 2;
		 * 
		 * filterPirs[2] = 1; filterPirs[5] = 1; filterPirs[12] = 1;
		 * filterPirs[13] = 1; filterPirs[16] = 1; filterPirs[17] = 1;
		 * filterPirs[19] = 1; filterPirs[50] = 1;
		 * 
		 * filterDoors[9] = 2; filterDoors[10] = 2;
		 * 
		 * filterItems[1] = 1; filterItems[5] = 2;
		 * 
		 * filterFaucets[0] = 1; filterFaucets[1] = 1;
		 */

		ArrayList<String> sfiltSensor = owle.getFilterSensor();
		ArrayList<Integer> temppir = new ArrayList<Integer>();
		ArrayList<Integer> tempitem = new ArrayList<Integer>();
		ArrayList<Integer> tempdoor = new ArrayList<Integer>();
		ArrayList<Integer> tempphone = new ArrayList<Integer>();
		ArrayList<Integer> tempfaucet = new ArrayList<Integer>();
		ArrayList<Integer> tempstove = new ArrayList<Integer>();

		for(String sens : sfiltSensor)
		{
			if(sens.startsWith("cPIR"))
			{
				String[] par=sens.split("_");
				int id = Integer.parseInt(par[0].replace("cPIR", ""))-1;
				if(filterPirs[id]==-1)
					if(par[1].equals("On"))
						filterPirs[id]=1;
					else
						filterPirs[id]=0;
				else
					filterPirs[id]=2;
				temppir.add(id);
				
			}
			if(sens.startsWith("cItem"))
			{
				String[] par=sens.split("_");
				int id = Integer.parseInt(par[0].replace("cItem", ""))-1;
				if(filterItems[id]==-1)
					if(par[1].equals("On"))
						filterItems[id]=1;
					else
						filterItems[id]=0;
				else
					filterItems[id]=2;
				tempitem.add(id);
			}
			if(sens.startsWith("cDoor"))
			{
				String[] par=sens.split("_");
				int id = Integer.parseInt(par[0].replace("cDoor", ""))-1;
				if(filterDoors[id]==-1)
					if(par[1].equals("Open"))
						filterDoors[id]=1;
					else
						filterDoors[id]=0;
				else
					filterDoors[id]=2;
				tempdoor.add(id);
			}
			if(sens.startsWith("cFaucet"))
			{
				String[] par=sens.split("_");
				int id = Integer.parseInt(par[0].replace("cFaucet", ""))-1;
				if(filterFaucets[id]==-1)
					if(par[1].equals("Open"))
						filterFaucets[id]=1;
					else
						filterFaucets[id]=0;
				else
					filterFaucets[id]=2;
				tempfaucet.add(id);
			}
			if(sens.startsWith("cStove"))
			{
				String[] par=sens.split("_");
				int id = Integer.parseInt(par[0].replace("cStove", ""))-1;
				if(filterStoves[id]==-1)
					if(par[1].equals("On"))
						filterStoves[id]=1;
					else
						filterStoves[id]=0;
				else
					filterStoves[id]=2;
				tempstove.add(id);
			}
			if(sens.startsWith("cPhone"))
			{
				String[] par=sens.split("_");
				int id = Integer.parseInt(par[0].replace("cPhone", ""))-1;
				if(filterPhones[id]==-1)
					if(par[1].equals("On"))
						filterPhones[id]=1;
					else
						filterPhones[id]=0;
				else
					filterPhones[id]=2;
				tempphone.add(id);
			}
		}
		
		interPirs=new int[temppir.size()];
		interItems=new int[tempitem.size()];
		interDoors=new int[tempdoor.size()];
		interStoves=new int[tempstove.size()];
		interFaucets=new int[tempfaucet.size()];
		interPhones=new int[tempphone.size()];
		
		for(int i=0; i<temppir.size(); i++)
			interPirs[i]=temppir.get(i);
		for(int i=0; i<tempitem.size(); i++)
			interItems[i]=tempitem.get(i);
		for(int i=0; i<tempdoor.size(); i++)
			interDoors[i]=tempdoor.get(i);
		for(int i=0; i<tempstove.size(); i++)
			interStoves[i]=tempstove.get(i);
		for(int i=0; i<tempfaucet.size(); i++)
			interFaucets[i]=tempfaucet.get(i);
		for(int i=0; i<tempphone.size(); i++)
			interPhones[i]=tempphone.get(i);
		
		
		
		// CREO GLI INDIVIDUI CHE RIEMPIRANNO GLI INTERVALLI
		for (int i = 0; i < interPirs.length; i++)
		{
			if (filterPirs[interPirs[i]] == 0 || filterPirs[interPirs[i]] == 2)
				owlHelp.createIndividual("cPIR" + String.valueOf(interPirs[i] + 1) + "_Off", "iPIR" + String.valueOf(interPirs[i] + 1) + "_Off");
			if (filterPirs[interPirs[i]] == 1 || filterPirs[interPirs[i]] == 2)
				owlHelp.createIndividual("cPIR" + String.valueOf(interPirs[i] + 1) + "_On", "iPIR" + String.valueOf(interPirs[i] + 1) + "_On");
		}

		for (int i = 0; i < interItems.length; i++)
		{
			if (filterItems[interItems[i]] == 0 || filterItems[interItems[i]] == 2)
				owlHelp.createIndividual("cItem" + String.valueOf(interItems[i] + 1) + "_Off", "iItem" + String.valueOf(interItems[i] + 1) + "_Off");
			if (filterItems[interItems[i]] == 1 || filterItems[interItems[i]] == 2)
				owlHelp.createIndividual("cItem" + String.valueOf(interItems[i] + 1) + "_On", "iItem" + String.valueOf(interItems[i] + 1) + "_On");
		}

		for (int i = 0; i < interDoors.length; i++)
		{
			if (filterDoors[interDoors[i]] == 0 || filterDoors[interDoors[i]] == 2)
				owlHelp.createIndividual("cDoor" + String.valueOf(interDoors[i] + 1) + "_Close", "iDoor" + String.valueOf(interDoors[i] + 1) + "_Close");
			if (filterDoors[interDoors[i]] == 1 || filterDoors[interDoors[i]] == 2)
				owlHelp.createIndividual("cDoor" + String.valueOf(interDoors[i] + 1) + "_Open", "iDoor" + String.valueOf(interDoors[i] + 1) + "_Open");
		}

		for (int i = 0; i < interPhones.length; i++)
		{
			if (filterPhones[interPhones[i]] == 0 || filterPhones[interPhones[i]] == 2)
				owlHelp.createIndividual("cPhone" + String.valueOf(interPhones[i] + 1) + "_Stop", "iPhone" + String.valueOf(interPhones[i] + 1) + "_Stop");
			if (filterPhones[interPhones[i]] == 1 || filterPhones[interPhones[i]] == 2)
				owlHelp.createIndividual("cPhone" + String.valueOf(interPhones[i] + 1) + "_Start", "iPhone" + String.valueOf(interPhones[i] + 1) + "_Start");
		}

		for (int i = 0; i < interFaucets.length; i++)
		{
			if (filterFaucets[interFaucets[i]] == 0 || filterFaucets[interFaucets[i]] == 2)
				owlHelp.createIndividual("cFaucet" + String.valueOf(interFaucets[i] + 1) + "_Close", "iFaucet" + String.valueOf(interFaucets[i] + 1) + "_Close");
			if (filterFaucets[interFaucets[i]] == 1 || filterFaucets[interFaucets[i]] == 2)
				owlHelp.createIndividual("cFaucet" + String.valueOf(interFaucets[i] + 1) + "_Open", "iFaucet" + String.valueOf(interFaucets[i] + 1) + "_Open");
		}
	}

	public void UpdateSensorVector(String sens, String val)
	{
		char c = sens.charAt(0);

		System.arraycopy(pirs, 0, prevpirs, 0, pirs.length);
		System.arraycopy(doors, 0, prevdoors, 0, doors.length);
		System.arraycopy(items, 0, previtems, 0, items.length);
		System.arraycopy(faucets, 0, prevfaucets, 0, faucets.length);
		System.arraycopy(stoves, 0, prevstoves, 0, stoves.length);
		System.arraycopy(phones, 0, prevphones, 0, phones.length);

		// boolean stoves[] = new boolean[3];
		if (faucets[0])
		{
			contaTap++;
			if (contaTap > 10)
			{
				faucets[0] = false;
				contaTap = 0;
			}
		}

		/*
		 * if (faucets[0]) faucets[0] = false; if (faucets[1]) faucets[1] =
		 * false; if (stoves[0]) stoves[0] = false;
		 */

		// PrintAndSave(sens + " " + val + "\n");

		switch (c)
		{
		case 'M':
		{
			Integer nSensor = Integer.parseInt(sens.substring(1, 3));
			if (nSensor > numPIR)
				break;
			if (val.contains("ON"))
				pirs[nSensor - 1] = true;
			else
				pirs[nSensor - 1] = false;

		}
			break;

		case 'D':
		{
			Integer nSensor = Integer.parseInt(sens.substring(1, 3));
			if (nSensor > numDoor)
				break;
			if (val.contains("OPEN"))
				doors[nSensor - 1] = true;
			else
				doors[nSensor - 1] = false;
		}
			break;

		case 'P':
		{
			Integer nSensor = Integer.parseInt(sens.substring(1, 3));
			if (val.contains("START"))
				phones[nSensor - 1] = true;
			else
				phones[nSensor - 1] = false;
		}
			break;

		case 'I':
		{
			Integer nSensor = Integer.parseInt(sens.substring(1, 3));
			if (nSensor > numItem)
				break;
			if (val.contains("ABSENT"))
				items[nSensor - 1] = true;
			else
				items[nSensor - 1] = false;
		}
			break;

		case 'A':
		{
			faucets[0] = true;
			faucets[1] = true;
			/*
			 * if (sens.compareTo("AD1-A") == 0) { stoves[0] =
			 * !stoves[0];//true; } if (sens.compareTo("AD1-B") == 0) {
			 * faucets[0] = !faucets[0];//true; } if (sens.compareTo("AD1-C") ==
			 * 0) { faucets[1] = !faucets[1];//true; }
			 */
		}
			break;
		}
	}

	private void SetBefore(String eventName, String LastEv, ArrayList<AddAxiom> axs)
	{
		axs.add(owlHelp.setIndividualObjectPropertyAxiom(eventName, "hasPredecessor", LastEv));
		/*
		 * for (String bef : ListaEventi)
		 * axs.add(owlHelp.setIndividualObjectPropertyAxiom(eventName,
		 * "hasPredecessor", bef));
		 */
	}

	public static void main(String[] args)
	{

		DateFormat formatter;
		formatter = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
		Date date = null;
		Date date2 = null;

		Calendar cal = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();

		String[] tests = { "p13", "p17", "p18", "p19", "p20", "p23", "p24", "p25", "p26", "p27", "p28", "p29", "p30", "p31", "p32", "p33", "p34" };

		for (int q = 0; q < tests.length; q++)
		{

			OWLHelpSmartHome owle = new OWLHelpSmartHome("CASAS_OWL.owl", "http://www.semanticweb.org/ontologies/2010/9/casaOwl.owl#", "Save" + tests[q] + ".txt");

			OWLSmartHome casa = new OWLSmartHome(owle);

			// casa.initMatlabFile();

			if (args.length > 0)
			{
				for (int i = 0; i < args.length; i++)
				{
					if (args[i].equals("nocontext"))
					{
						casa.printCont = false;
					}
					if (args[i].equals("nosituation"))
					{
						casa.printSit = false;
					}
					if (args[i].startsWith("removetime="))
					{
						casa.removeTime = Integer.parseInt(args[i].replace("removetime=", ""));
					}
				}
			}

			String str = null;
			String[] strA;

			BufferedReader in = null;

			casa.owlHelp.PrintAndSaveln("Start processing file " + tests[q] + ".interwoven");

			try
			{
				in = new BufferedReader(new FileReader("testData/" + tests[q] + ".interwoven"));
			} catch (FileNotFoundException e1)
			{
				// e1.printStackTrace();
			}
			try
			{
				str = in.readLine();
			} catch (IOException e2)
			{
				// TODO Auto-generated catch block
				// e2.printStackTrace();
			}

			try
			{
				date = (Date) formatter.parse(str.subSequence(0, 25).toString());
			} catch (ParseException e1)
			{
				// TODO Auto-generated catch block
				// e1.printStackTrace();
			}

			Long diff;

			try
			{

				while ((str = in.readLine()) != null)
				{

					// Divido la stringa
					strA = str.split(" ");

					Calendar sumcal = Calendar.getInstance();
					sumcal.setTime(new Date());

					try
					{
						date2 = (Date) formatter.parse(strA[0] + " " + strA[1]);

						cal.setTime(date);
						cal2.setTime(date2);
						date = date2;

					} catch (ParseException e)
					{
						// TODO Auto-generated catch block
						// e.printStackTrace();
					}

					diff = ((Long) (cal2.getTimeInMillis() - cal.getTimeInMillis()));

					casa.UpdateSensorVector(strA[2], strA[3]);

					casa.UpdateOntology((int) (cal2.getTimeInMillis() / 1000));

					if (diff != 0 && casa.sleepScale!=0)
					{
						System.out.println("Sleep for:" + diff.toString());
						try
						{
							Thread.sleep(diff/casa.sleepScale);
						} catch (InterruptedException e)
						{
							e.printStackTrace();
						}
					}

					String toPrint = "";
					if (casa.printCont && !casa.owlHelp.classContext.equals(""))
						toPrint += "Context: " + casa.owlHelp.classContext + "\n";
					if (casa.printSit && !casa.owlHelp.classSituation.equals(""))
						toPrint += "Situation: " + casa.owlHelp.classSituation.replace("Situation ", "") + "\n";

					if (!toPrint.equals(""))
					{
						toPrint = str.substring(0, 19) + "\n" + toPrint;
						toPrint += "Reasoning time " + casa.classtime + " ms\n";
						toPrint += "---------------------------------------------------\n";
						casa.owlHelp.PrintAndSave(toPrint);
						casa.owlHelp.classContext = "";
						casa.owlHelp.classSituation = "";
					}

				}
				in.close();
			} catch (IOException e)
			{
			}

			// casa.SaveOntology("endTest.owl");
			casa.owlHelp.PrintAndSaveln("end");
			// casa.SaveInference("inferredOnto.owl");
		}
	}

	private void CreateNewPeriodAxioms(String sensName, String eventName, int cal)
	{

		if (SaveLog)
			MyLog.scriviln("log.txt", "Creo periodo " + eventName);

		ArrayList<AddAxiom> axs = new ArrayList<AddAxiom>();

		// - CREO NUOVO ISTANTE
		axs.add(owlHelp.createIndividualAxiom("Interval", eventName));
		ListaEventi.add(eventName);

		mapEvents.put(eventName, cal);

		// - LO RIEMPO CON I SENSORI ATTIVI IN QUESTO ISTANTE
		axs.add(owlHelp.setIndividualObjectPropertyAxiom(eventName, "hasFluent", sensName));
		if (SaveLog)
			MyLog.scrivi("log.txt", "Aggiungo a " + eventName + " il ruolo Overlap con gli individui: " + sensName);
		// - PRENDO L'ELEMENTO CHE VIENE PRIMA E USO TUTTI I SUOI RIEMPITORI
		// COME RIEMPITORI DEL NUOVO
		if (sequence.size() > 0)
		{
			String LastEv = sequence.get(sequence.size() - 1);

			ArrayList<String> strInds = owlHelp.getObjectPropertyValuesName(LastEv, "hasFluent");

			for (String str : strInds)
			{
				if (SaveLog)
					MyLog.scrivi("log.txt", ", " + str);
				axs.add(owlHelp.setIndividualObjectPropertyAxiom(eventName, "hasFluent", str));
			}
			if (SaveLog)
				MyLog.scriviln("log.txt", "");

			if (SaveLog)
				MyLog.scriviln("log.txt", "Aggiungo a " + eventName + " il ruolo Before con l'ultimo evento: " + LastEv);

			// axs.add(owlHelp.setIndividualObjectPropertyAxiom(eventName,
			// "hasPredecessor", LastEv));
			SetBefore(eventName, LastEv, axs);

			if (!owlHelp.lastSituation.equals(""))
				SetBefore(eventName, LastEv, axs);
			// axs.add(owlHelp.setIndividualObjectPropertyAxiom(eventName,
			// "hasPredecessor", owlHelp.lastSituation));

			/*
			 * for(OWLNamedIndividual owlnam :
			 * owlHelp.retrieveClassInstances("Interval")) { String
			 * sNam=owlHelp.getBaseName(owlnam.toString()); if(eventName!=sNam)
			 * axs.add(owlHelp.setIndividualObjectPropertyAxiom(eventName,
			 * "hasPredecessor", sNam)); }
			 */

		}

		if (SaveLog)
			MyLog.scriviln("log.txt", "Aggiungo a " + eventName + " il ruolo Date con : " + cal);

		axs.add(owlHelp.setIndividualDataPropertyAxiom(eventName, "itime", cal));
		// axs.add(owlHelp.setIndividualDataProperty(eventName, "Date", cal));

		// - AGGIORNO IL VETTORE CHE CONTIENE GLI ISTANTI
		sequence.add(eventName);
		if (SaveLog)
			MyLog.scriviln("log.txt", "Aggiungo " + eventName + " all'elenco di situazioni attive");

		owlHelp.persistAxioms(axs);

		if (SaveLog)
			MyLog.scriviln("log.txt", "FINE CREAZIONE EVENTO");

	}

	private void UpdatePeriodsAxiom(String sensName)
	{
		ArrayList<AddAxiom> axs = new ArrayList<AddAxiom>();

		// SE IL SENSORE E' DIVENTATO INATTIVO
		// GUARDO DALLA LISTA GLI EVENTI SUCCESSIVI
		// RICREO I SUCCESSIVI SENZA IL SENSORE INATTIVO CON IL PERIODO GIUSTO
		// ELIMINO I PERIODI CON IL SENSORE

		// ArrayList<String> newInterval= new ArrayList<String>();

		if (SaveLog)
			MyLog.scriviln("log.txt", "Termino gli eventi che contengono " + sensName + " e li ricreo");

		boolean first = false;
		for (int s = 0; s < sequence.size(); s++)
		{

			// String cal = owlHelp.getDataPropertyValues(sequence.get(s),
			// "Date");
			int cal = owlHelp.getDataPropertyIntValues(sequence.get(s), "itime");

			ArrayList<String> objprops = owlHelp.getObjectPropertyValuesName(sequence.get(s), "hasFluent");
			if (objprops.contains(sensName))
			{
				String newEventName = sequence.get(s) + "b" + String.valueOf(instant);
				if (SaveLog)
					MyLog.scriviln("log.txt", "Termino " + sequence.get(s) + " e creo " + newEventName);
				sequence.remove(s);

				if (first)
				{
					// CREO LA COPIA CON TUTTI I SENSORI TRANNE IL CANCELLATO
					axs.add(owlHelp.createIndividualAxiom("Interval", newEventName));
					ListaEventi.add(newEventName);

					mapEvents.put(newEventName, cal);
					if (SaveLog)
						MyLog.scrivi("log.txt", "Aggiungo a " + newEventName + " il ruolo Overlap con gli individui: ");
					for (int ob = 0; ob < objprops.size(); ob++)
					{
						if (!objprops.get(ob).equals(sensName))
						{
							axs.add(owlHelp.setIndividualObjectPropertyAxiom(newEventName, "hasFluent", objprops.get(ob)));
							if (SaveLog)
								MyLog.scrivi("log.txt", objprops.get(ob));
						}
					}
					if (SaveLog)
						MyLog.scriviln("log.txt", "");

					sequence.add(s, newEventName);

					// axs.add(owlHelp.setIndividualObjectPropertyAxiom(newEventName,
					// "hasPredecessor", sequence.get(s-1)));
					SetBefore(newEventName, sequence.get(s - 1), axs);
					if (SaveLog)
						MyLog.scriviln("log.txt", "Aggiungo a " + newEventName + " il ruolo Before con: " + sequence.get(s - 1));

					if (!owlHelp.lastSituation.equals(""))
						SetBefore(newEventName, owlHelp.lastSituation, axs);
					// axs.add(owlHelp.setIndividualObjectPropertyAxiom(newEventName,
					// "hasPredecessor", owlHelp.lastSituation));

					/*
					 * for(OWLNamedIndividual owlnam :
					 * owlHelp.retrieveClassInstances("Interval")) { String
					 * sNam=owlHelp.getBaseName(owlnam.toString());
					 * 
					 * if(EventInstant(newEventName)>EventInstant(sNam))
					 * axs.add(
					 * owlHelp.setIndividualObjectPropertyAxiom(newEventName,
					 * "hasPredecessor", sNam)); }
					 */

					axs.add(owlHelp.setIndividualDataPropertyAxiom(newEventName, "itime", cal));
					if (SaveLog)
						MyLog.scriviln("log.txt", "Aggiungo a " + newEventName + " il ruolo Date con: " + cal);
				} else
					first = true;
			}
		}

		owlHelp.persistAxioms(axs);
	}

	private void CreateAndUpdatePeriodsAxioms(String updateSensName, String eventName, String removeSensName, int cal)
	{
		ArrayList<AddAxiom> axs = new ArrayList<AddAxiom>();

		// - CREO NUOVO ISTANTE
		axs.add(owlHelp.createIndividualAxiom("Interval", eventName));
		ListaEventi.add(eventName);
		if (SaveLog)
			MyLog.scriviln("log.txt", "Creo periodo " + eventName);

		mapEvents.put(eventName, cal);
		// - LO RIEMPO CON I SENSORI ATTIVI IN QUESTO ISTANTE
		axs.add(owlHelp.setIndividualObjectPropertyAxiom(eventName, "hasFluent", updateSensName));
		if (SaveLog)
			MyLog.scrivi("log.txt", "Aggiungo a " + eventName + " il ruolo Overlap, escludendo " + removeSensName + ", con gli individui: " + updateSensName);
		// - PRENDO L'ELEMENTO CHE VIENE PRIMA E USO TUTTI I SUOI RIEMPITORI
		// COME RIEMPITORI DEL NUOVO
		// TRANNE L'ELEMENTO CHE NON è PIù ATTIVO
		if (sequence.size() > 0)
		{
			String LastEv = sequence.get(sequence.size() - 1);

			ArrayList<String> strInds = owlHelp.getObjectPropertyValuesName(LastEv, "hasFluent");
			for (String str : strInds)
			{
				if (!str.equals(removeSensName))
				{
					if (SaveLog)
						MyLog.scrivi("log.txt", ", " + str);
					axs.add(owlHelp.setIndividualObjectPropertyAxiom(eventName, "hasFluent", str));
				}
			}
			if (SaveLog)
				MyLog.scriviln("log.txt", "");

			SetBefore(eventName, LastEv, axs);
			// axs.add(owlHelp.setIndividualObjectPropertyAxiom(eventName,
			// "hasPredecessor", LastEv));
			if (SaveLog)
				MyLog.scriviln("log.txt", "Aggiungo a " + eventName + " il ruolo Before con l'ultimo evento: " + LastEv);

			if (!owlHelp.lastSituation.equals(""))
				SetBefore(eventName, owlHelp.lastSituation, axs);
			// axs.add(owlHelp.setIndividualObjectPropertyAxiom(eventName,
			// "hasPredecessor", owlHelp.lastSituation));

			/*
			 * for(OWLNamedIndividual owlnam :
			 * owlHelp.retrieveClassInstances("Interval")) { String
			 * sNam=owlHelp.getBaseName(owlnam.toString()); if(eventName!=sNam)
			 * axs.add(owlHelp.setIndividualObjectPropertyAxiom(eventName,
			 * "hasPredecessor", sNam)); }
			 */

		}

		axs.add(owlHelp.setIndividualDataPropertyAxiom(eventName, "itime", cal));
		if (SaveLog)
			MyLog.scriviln("log.txt", "Aggiungo a " + eventName + " il ruolo Date con l'ultimo evento: " + cal);

		// - AGGIORNO IL VETTORE CHE CONTIENE GLI ISTANTI
		sequence.add(eventName);

		owlHelp.persistAxioms(axs);

		UpdatePeriodsAxiom(removeSensName);

	}

	private void UpdateOntology(int cal)
	{
		boolean novita = false;

		int a;

		String eventName = "Event_t" + String.valueOf(instant);

		if (SaveLog)
			MyLog.scriviln("log.txt", "UPDATE ONTOLOGY");

		owlHelp.UpdateIntervals(cal);

		// /////////////PIR//////////////////////
		for (int i = 0; i < interPirs.length; i++)
		{
			a = interPirs[i];

			if (prevpirs[a] != pirs[a] || instant == 0)
			{
				novita = true;

				if (SaveLog)
					MyLog.scriviln("log.txt", "PIR " + a + " è cambiato");
				// SE IL SENSORE E' DIVENTATO POSITIVO AGGIUNGO UN INDIVIDUO
				// ISTANTE CHE LO CONTIENE INSIEME AGLI ALTRI
				if ((pirs[a] ? 1 : 0) == filterPirs[a] || (filterPirs[a] == 2 && instant == 0))
				{
					String sensName = "iPIR" + String.valueOf(a + 1) + (pirs[a] ? "_On" : "_Off");
					eventName = eventName + "-" + String.valueOf(a + 1);
					CreateNewPeriodAxioms(sensName, eventName, cal);
				} else if (instant != 0)
				{
					String sensName = "iPIR" + String.valueOf(a + 1) + (prevpirs[a] ? "_On" : "_Off");
					if (filterPirs[a] == 2)
					{
						String UpdateSensName = "iPIR" + String.valueOf(a + 1) + (pirs[a] ? "_On" : "_Off");
						eventName = eventName + "-" + String.valueOf(a + 1);
						CreateAndUpdatePeriodsAxioms(UpdateSensName, eventName, sensName, cal);
					} else
						UpdatePeriodsAxiom(sensName);

				}
			}
		}
		// ////////////////////////////////////////////////

		// /////////////DOOR//////////////////////
		for (int i = 0; i < interDoors.length; i++)
		{
			a = interDoors[i];
			if (prevdoors[a] != doors[a] || instant == 0)
			{
				if (SaveLog)
					MyLog.scriviln("log.txt", "Door " + a + " è cambiato");
				novita = true;

				// SE IL SENSORE E' DIVENTATO POSITIVO AGGIUNGO UN INDIVIDUO
				// ISTANTE CHE LO CONTIENE INSIEME AGLI ALTRI
				if ((doors[a] ? 1 : 0) == filterDoors[a] || (filterDoors[a] == 2 && instant == 0))
				{
					String sensName = "iDoor" + String.valueOf(a + 1) + (doors[a] ? "_Open" : "_Close");
					eventName = eventName + "-" + String.valueOf(a + 1);
					CreateNewPeriodAxioms(sensName, eventName, cal);

				} else if (instant != 0)
				{
					String sensName = "iDoor" + String.valueOf(a + 1) + (prevdoors[a] ? "_Open" : "_Close");
					if (filterDoors[a] == 2)
					{
						String UpdateSensName = "iDoor" + String.valueOf(a + 1) + (doors[a] ? "_Open" : "_Close");
						eventName = eventName + "-" + String.valueOf(a + 1);
						CreateAndUpdatePeriodsAxioms(UpdateSensName, eventName, sensName, cal);
					} else
						UpdatePeriodsAxiom(sensName);
				}
			}
		}
		// ////////////////////////////////////////////////

		// /////////////ITEM//////////////////////
		for (int i = 0; i < interItems.length; i++)
		{
			a = interItems[i];
			if (previtems[a] != items[a] || instant == 0)
			{

				if (SaveLog)
					MyLog.scriviln("log.txt", "Item " + a + " è cambiato");
				novita = true;

				// SE IL SENSORE E' DIVENTATO POSITIVO AGGIUNGO UN INDIVIDUO
				// ISTANTE CHE LO CONTIENE INSIEME AGLI ALTRI
				if ((items[a] ? 1 : 0) == filterItems[a] || (filterItems[a] == 2 && instant == 0))
				{
					String sensName = "iItem" + String.valueOf(a + 1) + (items[a] ? "_On" : "_Off");
					eventName = eventName + "-" + String.valueOf(a + 1);
					CreateNewPeriodAxioms(sensName, eventName, cal);

				} else if (instant != 0)
				{
					String sensName = "iItem" + String.valueOf(a + 1) + (previtems[a] ? "_On" : "_Off");
					if (filterItems[a] == 2)
					{
						String UpdateSensName = "iItem" + String.valueOf(a + 1) + (items[a] ? "_On" : "_Off");
						eventName = eventName + "-" + String.valueOf(a + 1);
						CreateAndUpdatePeriodsAxioms(UpdateSensName, eventName, sensName, cal);
					} else
						UpdatePeriodsAxiom(sensName);
				}
			}
		}
		// ////////////////////////////////////////////////

		// /////////////PHONE//////////////////////
		for (int i = 0; i < interPhones.length; i++)
		{
			a = interPhones[i];
			if (prevphones[a] != phones[a] || instant == 0)
			{

				if (SaveLog)
					MyLog.scriviln("log.txt", "Phone " + a + " è cambiato");
				novita = true;

				// SE IL SENSORE E' DIVENTATO POSITIVO AGGIUNGO UN INDIVIDUO
				// ISTANTE CHE LO CONTIENE INSIEME AGLI ALTRI
				if ((phones[a] ? 1 : 0) == filterPhones[a] || (filterPhones[a] == 2 && instant == 0))
				{
					String sensName = "iPhone" + String.valueOf(a + 1) + (phones[a] ? "_Start" : "_Stop");
					eventName = eventName + "-" + String.valueOf(a + 1);
					CreateNewPeriodAxioms(sensName, eventName, cal);

				} else if (instant != 0)
				{
					String sensName = "iPhone" + String.valueOf(a + 1) + (prevphones[a] ? "_Start" : "_Stop");
					if (filterPhones[a] == 2)
					{
						String UpdateSensName = "iPhone" + String.valueOf(a + 1) + (phones[a] ? "_Start" : "_Stop");
						eventName = eventName + "-" + String.valueOf(a + 1);
						CreateAndUpdatePeriodsAxioms(UpdateSensName, eventName, sensName, cal);
					} else
						UpdatePeriodsAxiom(sensName);
				}
			}
		}
		// ////////////////////////////////////////////////

		// /////////////FAUCET//////////////////////
		for (int i = 0; i < interFaucets.length; i++)
		{
			a = interFaucets[i];
			if (prevfaucets[a] != faucets[a] || instant == 0)
			{
				novita = true;
				if (SaveLog)
					MyLog.scriviln("log.txt", "Faucet " + a + " è cambiato");
				// SE IL SENSORE E' DIVENTATO POSITIVO AGGIUNGO UN INDIVIDUO
				// ISTANTE CHE LO CONTIENE INSIEME AGLI ALTRI
				if ((faucets[a] ? 1 : 0) == filterFaucets[a] || (filterFaucets[a] == 2 && instant == 0))
				{
					String sensName = "iFaucet" + String.valueOf(a + 1) + (faucets[a] ? "_Open" : "_Close");
					eventName = eventName + "-" + String.valueOf(a + 1);
					CreateNewPeriodAxioms(sensName, eventName, cal);

				} else if (instant != 0)
				{
					String sensName = "iFaucet" + String.valueOf(a + 1) + (prevfaucets[a] ? "_Open" : "_Close");
					if (filterFaucets[a] == 2)
					{
						String UpdateSensName = "iFaucet" + String.valueOf(a + 1) + (faucets[a] ? "_Open" : "_Close");
						eventName = eventName + "-" + String.valueOf(a + 1);
						CreateAndUpdatePeriodsAxioms(UpdateSensName, eventName, sensName, cal);
					} else
						UpdatePeriodsAxiom(sensName);
				}
			}
		}
		// ////////////////////////////////////////////////

		if (novita)
		{
			instant++;

			ArrayList<AddAxiom> timeax = new ArrayList<AddAxiom>();

			// for(int i =0; i<sequence.size();i++)
			// timeax.add(updatePeriodTimeAxiom(sequence.get(i), cal));

			for (int i = 0; i < sequence.size(); i++)
			{
				AddAxiom tax = updateSubClassPeriodAxiom(sequence.get(i), cal);
				if (tax != null)
					timeax.add(tax);
			}

			owlHelp.persistAxioms(timeax);

			// ELIMINAZIONE EVENTI VECCHI
			RemoveOldEvents(cal);

			owlHelp.computeInference();

			Date date = new Date();

			long time = date.getTime();

			/*
			 * for (int i = 0; i < sequence.size(); i++) { //
			 * System.out.print(sequence.get(i) + " ");
			 * 
			 * owlHelp.classifyEvent(sequence.get(i));
			 * 
			 * }
			 */

			if (sequence.size() > 0)
				owlHelp.classifyEvent(sequence.get(sequence.size() - 1));

			date = new Date();
			long diffclass = date.getTime() - time;
			checks++;

			/*
			 * FileWriter file; try { file = new FileWriter("dati.csv", true);
			 * file.append(String.valueOf(diffclass) + ";" +
			 * String.valueOf(owlHelp.createdIndividual) + "\n"); file.close();
			 * } catch (IOException e) { // TODO Auto-generated catch block
			 * //e.printStackTrace(); }
			 */

			// timeclassify+=(int)diffclass;

			/*
			 * if(diffclass<mintime) mintime=(int)diffclass;
			 * if(diffclass>maxtime) maxtime=(int)diffclass;
			 */

			classtime = diffclass;
			// owlHelp.PrintAndSaveln("Classified in " +
			// String.valueOf(diffclass));

			if (SaveLog)
				MyLog.scriviln("log.txt", "Classificazione effettuata in " + diffclass + " secondi");
			if (SaveLog)
				MyLog.scriviln("log.txt", "Numero eventi attivi: " + sequence.size());
			if (SaveLog)
				MyLog.scriviln("log.txt", "FINE AGGIORNAMENTO ONTOLOGIA");

		}

	}

	private void RemoveOldEvents(int timenow)
	{
		// System.out.println("Recupero tutti gli eventi");

		// Set<OWLNamedIndividual>
		// setind=owlHelp.retrieveClassInstances("5min");

		// System.out.println("Numero Individui "+String.valueOf(setind.size()));

		// for(OWLNamedIndividual owlnam : setind)

		ArrayList<String> events = new ArrayList<String>();
		events.addAll(mapEvents.keySet());

		for (String event : events)
		{
			// String event=owlHelp.getBaseName(owlnam.toString());

			// System.out.println(event);

			// if(event.startsWith("Event"))
			// {
			// String timeev = owlHelp.getDataPropertyValues(event, "Date");
			int timeev = mapEvents.get(event);

			int diff = timenow - timeev;

			if (diff > removeTime)
			{
				// if(SaveLog)MyLog.scriviln("log.txt",
				// "L'evento "+event+" è vero da "+diff+" secondi, verrà eliminato");

				// owlHelp.PrintAndSaveln("Rimuovo " + event);

				owlHelp.removeIndividual(event);
				ListaEventi.remove(event);

				owlHelp.createdIndividual--;

				mapEvents.remove(event);

				if (sequence.contains(event))
					sequence.remove(event);
			}
		}
		// }
	}

	public AddAxiom updatePeriodTimeAxiom(String event, String timenow)
	{

		String timeev = owlHelp.getDataPropertyValues(event, "itime");

		DateFormat formatter;
		formatter = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
		Date date = null;
		Date date2 = null;

		Calendar cal = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();

		try
		{
			date2 = (Date) formatter.parse(timenow);
			date = (Date) formatter.parse(timeev);
			cal.setTime(date);
			cal2.setTime(date2);

		} catch (ParseException e)
		{
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}

		Long diff = ((Long) (cal2.getTimeInMillis() - cal.getTimeInMillis()) / 1000);

		return owlHelp.setIndividualDataPropertyAxiom(event, "PeriodValue", diff.intValue());
	}

	public AddAxiom updateSubClassPeriodAxiom(String event, int timenow)
	{

		int t = owlHelp.getDataPropertyIntValues(event, "itime");

		int diff = timenow - t;

		if (diff >= 300)
		{
			if (SaveLog)
				MyLog.scriviln("log.txt", "L'evento " + event + " è vero da " + diff + " secondi. Aggiungo a " + event + " la classe 5min");
			return owlHelp.SetSubClassAxiom("LongherThan5min", event);
		}
		if (diff >= 60)
		{
			if (SaveLog)
				MyLog.scriviln("log.txt", "L'evento " + event + " è vero da " + diff + " secondi. Aggiungo a " + event + " la classe 1min");
			return owlHelp.SetSubClassAxiom("LongherThan1min", event);
		}
		if (diff >= 30)
		{
			if (SaveLog)
				MyLog.scriviln("log.txt", "L'evento " + event + " è vero da " + diff + " secondi. Aggiungo a " + event + " la classe 30sec");
			return owlHelp.SetSubClassAxiom("LongherThan30sec", event);
		}

		return null;
	}

}
