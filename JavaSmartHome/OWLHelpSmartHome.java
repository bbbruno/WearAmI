import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.vocab.OWLFacet;

public class OWLHelpSmartHome extends OWLHelper
{

	public String[] sitArray;// = { "PreparingMeal", "MakingPhonecall", "CleaningHouse", "HavingMeal", "RefillingDispenser", "WateringPlants", "Situation" };

	public Hashtable<String, ArrayList<String>> sitmap = new Hashtable<String, ArrayList<String>>();
	public String lastSituation = "";
	public ArrayList<String> activeSituation;

	public OWLHelpSmartHome(String ontofile, String base, String savename) {
		super(ontofile, base, savename);

		activeSituation = new ArrayList<String>();

		OWLClass subclassType = factory.getOWLClass(IRI.create(baseString + "Situation"));

		NodeSet<OWLClass> sitcl = hermit.getSubClasses(subclassType, false);
		Set<OWLClass> sitset = sitcl.getFlattened();
		ArrayList<String> sittemp=new ArrayList<String>();
		for (OWLClass cl : sitset)
		{
			sittemp.add(getBaseName(cl.toString()));
			System.out.println(getBaseName(cl.toString()));
		}
		sitArray=new String[sittemp.size()];
		sitArray=sittemp.toArray(sitArray);
		
	}

	public ArrayList<String> getFilterSensor()
	{
		OWLClass contclass = factory.getOWLClass(IRI.create(baseString + "Context"));
		
		ArrayList<String> arr = new ArrayList<String>();

		NodeSet<OWLClass> contnode = hermit.getSubClasses(contclass, false);
		Set<OWLClass> contset = contnode.getFlattened();
		for (OWLClass cl : contset)
		{
			
			//OWLClass fir = factory.getOWLClass(IRI.create(cl));
			for (OWLClassAxiom axi : ontology.getAxioms(cl))
			{
				if (axi.toString().startsWith("EquivalentClasses"))
				{
					String[] axis = axi.toString().replace("<", "").replace(">", "").replace(baseString, "").split(" ");
					for (int i = 0; i < axis.length; i++)
					{
						// System.out.print(axis[i]+"-");
						if (axis[i].equals("hasFluent"))
						{
							arr.add(axis[i + 1].replace(")", ""));
							System.out.println(axis[i + 1].replace(")", ""));
						}
					}
				}
				// System.out.println(axi.toString().replace("<","").replace(">","").replace(baseString,
				// ""));
			}
		}
		return arr;
	}

	public void classifyEvent(String event)
	{

		NodeSet<OWLClass> nodes = hermit.getTypes(factory.getOWLNamedIndividual(IRI.create(baseString + event)), false);
		Set<OWLClass> classes = nodes.getFlattened();

		String Sit = "";
		String cont = "";

		ArrayList<String> nowSitArr = new ArrayList<String>();

		for (OWLClass oc : classes)
		{
			if (debugPr)
				PrintAndSave(getBaseName(oc.toString()) + " ");

			if (SaveLog)
				MyLog.scrivi("log.txt", ", " + getBaseName(oc.toString()));

			boolean bsit = false;

			for (int i = 0; i < sitArray.length; i++)
			{
				if (sitArray[i].equals(getBaseName(oc.toString())))
				{
					bsit = true;
					nowSitArr.add(getBaseName(oc.toString()));
					break;
				}
			}
			if (!bsit)
				cont += getBaseName(oc.toString()) + " ";

		}

		for (String sitname : nowSitArr)
		{
			if (!activeSituation.contains(sitname))
			{
				activeSituation.add(sitname);
				Sit += sitname + " ";
			}
		}

		for (int i = 0; i < activeSituation.size(); i++)
		{
			if (!nowSitArr.contains(activeSituation.get(i)))
			{
				activeSituation.remove(activeSituation.get(i));
				i--;
			}
		}

		classContext = cont;
		classSituation = Sit;
	}

	public void UpdateIntervals(int cal)
	{

		ArrayList<AddAxiom> interAx = new ArrayList<AddAxiom>();

		if (prevAxiom != null)
			manager.applyChange(new RemoveAxiom(ontology, prevAxiom));
		if (prevAxiom2 != null)
			manager.applyChange(new RemoveAxiom(ontology, prevAxiom2));

		{
			// Entro 2 minuti
			OWLClass min2class = factory.getOWLClass(IRI.create(baseString + "CloserThan2min"));

			OWLDataProperty timeline = factory.getOWLDataProperty(IRI.create(baseString + "itime"));
			OWLDatatype intDatatype = factory.getIntegerOWLDatatype();
			OWLLiteral sec = factory.getOWLLiteral(cal - 180);
			OWLFacet facet = OWLFacet.MIN_INCLUSIVE;
			OWLDataRange sec120 = factory.getOWLDatatypeRestriction(intDatatype, facet, sec);

			OWLClassExpression entro2min = factory.getOWLDataSomeValuesFrom(timeline, sec120);

			prevAxiom = factory.getOWLEquivalentClassesAxiom(min2class, entro2min);

			interAx.add(new AddAxiom(ontology, prevAxiom));
			// manager.applyChange(new AddAxiom(ontology, prevAxiom));
		}

		{
			// Dopo 30secondi
			OWLClass dop30class = factory.getOWLClass(IRI.create(baseString + "FartherThan30sec"));

			OWLDataProperty timeline2 = factory.getOWLDataProperty(IRI.create(baseString + "itime"));
			OWLDatatype intDatatype2 = factory.getIntegerOWLDatatype();
			OWLLiteral sec2 = factory.getOWLLiteral(cal - 30);
			OWLFacet facet2 = OWLFacet.MAX_INCLUSIVE;
			OWLDataRange sec30 = factory.getOWLDatatypeRestriction(intDatatype2, facet2, sec2);

			OWLClassExpression dopo30sec = factory.getOWLDataSomeValuesFrom(timeline2, sec30);

			prevAxiom2 = factory.getOWLEquivalentClassesAxiom(dop30class, dopo30sec);

			interAx.add(new AddAxiom(ontology, prevAxiom2));
		}

		manager.applyChanges(interAx);

	}

}
