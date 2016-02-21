import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Set;

import org.semanticweb.HermiT.Reasoner.ReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;
import org.semanticweb.owlapi.util.InferredSubClassAxiomGenerator;
import org.semanticweb.owlapi.util.OWLEntityRemover;

public class OWLHelper
{

	OWLEquivalentClassesAxiom prevAxiom = null;
	OWLEquivalentClassesAxiom prevAxiom2 = null;

	boolean SaveLog = false;
	boolean debugPr = false;
	
	public String classContext; 
	public String classSituation;

	
	public int createdIndividual = 0;

	protected String baseString;

	protected OWLOntologyManager manager;
	protected OWLOntology ontology;
	protected OWLDataFactory factory;
	protected OWLReasoner hermit;
	protected OWLReasonerFactory reasonerFactory;

	private String SaverName;

	public OWLHelper(String ontofile, String base, String savename) {

		SaverName = savename;

		manager = OWLManager.createOWLOntologyManager();

		File inputOntologyFile = new File(ontofile);
		// We use the OWL API to load the ontology.
		try
		{
			ontology = manager.loadOntologyFromOntologyDocument(inputOntologyFile);
		} catch (OWLOntologyCreationException e)
		{
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}

		factory = manager.getOWLDataFactory();

		// HERMIT Instance
		reasonerFactory = new ReasonerFactory();

		hermit = reasonerFactory.createNonBufferingReasoner(ontology);

		baseString = base;
	}

	public String getBaseString()
	{
		return baseString;
	}

	public void setBaseString(String baseString)
	{
		this.baseString = baseString;
	}

	public String getBaseName(String ur)
	{
		return ((ur.replace("<", "")).replace(">", "")).replace(baseString, "");
	}

	public OWLIndividual createIndividual(String type, String name)
	{
		createdIndividual++;

		if(debugPr) System.out.println(type + " - " + name+ "created");

		// Individual class IRI
		IRI iriClassType = IRI.create(baseString + type);

		// Individual class
		OWLClass classClassType = factory.getOWLClass(iriClassType);

		// create individual
		OWLIndividual indIndividual = factory.getOWLNamedIndividual(IRI.create(baseString + name));

		// Axiom for individual creation
		OWLAxiom axiom = factory.getOWLClassAssertionAxiom(classClassType, indIndividual);

		AddAxiom addax = new AddAxiom(ontology, axiom);

		manager.applyChange(addax);

		return indIndividual;
	}

	public AddAxiom createIndividualAxiom(String type, String name)
	{
		createdIndividual++;
		if(debugPr) System.out.println(">>>>>>Creo l'individuo "+name+">>>>>>di tipo "+type);

		IRI iriClassType = IRI.create(baseString + type);

		OWLClass classClassType = factory.getOWLClass(iriClassType);

		OWLIndividual indIndividual = factory.getOWLNamedIndividual(IRI.create(baseString + name));

		OWLAxiom axiom = factory.getOWLClassAssertionAxiom(classClassType, indIndividual);

		AddAxiom addax = new AddAxiom(ontology, axiom);

		return addax;

	}

	public Set<OWLNamedIndividual> retrieveClassInstances(String sClass)
	{
		IRI iriClassType = IRI.create(baseString + sClass);
		OWLClass clClass = factory.getOWLClass(iriClassType);

		hermit.precomputeInferences();

		return hermit.getInstances(clClass, false).getFlattened();
	}

	public ArrayList<String> getObjectPropertyValuesName(String ind, String prop)
	{
		OWLNamedIndividual indIndividual = factory.getOWLNamedIndividual(IRI.create(baseString + ind));

		OWLObjectProperty property = factory.getOWLObjectProperty(IRI.create(baseString + prop));

		Set<OWLNamedIndividual> inds = hermit.getObjectPropertyValues(indIndividual, property).getFlattened();

		ArrayList<String> strInds = new ArrayList<String>();

		for (OWLNamedIndividual nam : inds)
			strInds.add(getBaseName(nam.toString()));

		return strInds;

	}

	public String getDataPropertyValues(String ind, String prop)
	{
		OWLNamedIndividual indIndividual = factory.getOWLNamedIndividual(IRI.create(baseString + ind));

		OWLDataProperty property = factory.getOWLDataProperty(IRI.create(baseString + prop));

		return hermit.getDataPropertyValues(indIndividual, property).iterator().next().toString().replace("^^xsd:string", "").replace("\"", "");

	}

	public int getDataPropertyIntValues(String ind, String prop)
	{
		OWLNamedIndividual indIndividual = factory.getOWLNamedIndividual(IRI.create(baseString + ind));

		OWLDataProperty property = factory.getOWLDataProperty(IRI.create(baseString + prop));

		Set<OWLLiteral> sol = hermit.getDataPropertyValues(indIndividual.asOWLNamedIndividual(), property);

		if (sol.size() > 0)
		{
			return Integer.parseInt(sol.iterator().next().getLiteral());
		} else
			return -1;
	}

	// Remove the actual DataProperty and insert a new one with the new value
	public void setIndividualDataProperty(OWLIndividual indIndividual, OWLDataProperty property, int Value)
	{
		Set<OWLLiteral> sol = hermit.getDataPropertyValues(indIndividual.asOWLNamedIndividual(), property);

		if (sol.size() > 0)
		{
			int r = Integer.parseInt(sol.iterator().next().getLiteral());
			OWLDataPropertyAssertionAxiom assertion = factory.getOWLDataPropertyAssertionAxiom(property, indIndividual, r);

			RemoveAxiom remAxiomChange = new RemoveAxiom(ontology, assertion);
			manager.applyChange(remAxiomChange);
		}

		OWLDataPropertyAssertionAxiom assertion = factory.getOWLDataPropertyAssertionAxiom(property, indIndividual, Value);

		AddAxiom addAxiomChange = new AddAxiom(ontology, assertion);

		manager.applyChange(addAxiomChange);
	}

	public AddAxiom setIndividualDataPropertyAxiom(String ind, String prop, int Value)
	{

		OWLNamedIndividual indIndividual = factory.getOWLNamedIndividual(IRI.create(baseString + ind));

		OWLDataProperty property = factory.getOWLDataProperty(IRI.create(baseString + prop));

		try
		{
			Set<OWLLiteral> sol = hermit.getDataPropertyValues(indIndividual.asOWLNamedIndividual(), property);

			if (sol.size() > 0)
			{
				int r = Integer.parseInt(sol.iterator().next().getLiteral());
				
				OWLDataPropertyAssertionAxiom assertion = factory.getOWLDataPropertyAssertionAxiom(property, indIndividual, r);

				RemoveAxiom remAxiomChange = new RemoveAxiom(ontology, assertion);
				manager.applyChange(remAxiomChange);
			}

		} catch (NullPointerException e)
		{
			
		}

		OWLDataPropertyAssertionAxiom assertion = factory.getOWLDataPropertyAssertionAxiom(property, indIndividual, Value);

		AddAxiom addAxiomChange = new AddAxiom(ontology, assertion);

		return addAxiomChange;

	}

	public void setIndividualDataProperty(String ind, String prop, int Value)
	{

		OWLIndividual indIndividual = factory.getOWLNamedIndividual(IRI.create(baseString + ind));

		OWLDataProperty property = factory.getOWLDataProperty(IRI.create(baseString + prop));

		setIndividualDataProperty(indIndividual, property, Value);

	}

	public AddAxiom setIndividualDataProperty(String ind, String prop, String Value)
	{

		OWLIndividual indIndividual = factory.getOWLNamedIndividual(IRI.create(baseString + ind));

		OWLDataProperty property = factory.getOWLDataProperty(IRI.create(baseString + prop));

		OWLDataPropertyAssertionAxiom assertion = factory.getOWLDataPropertyAssertionAxiom(property, indIndividual, Value);

		AddAxiom addAxiomChange = new AddAxiom(ontology, assertion);

		return addAxiomChange;

	}

	public void setIndividualObjectProperty(OWLIndividual indIndividual, OWLObjectProperty property, OWLIndividual propval)
	{

		OWLObjectPropertyAssertionAxiom assertion = factory.getOWLObjectPropertyAssertionAxiom(property, indIndividual, propval);

		AddAxiom addAxiomChange = new AddAxiom(ontology, assertion);

		manager.applyChange(addAxiomChange);

	}

	public AddAxiom setIndividualObjectPropertyAxiom(String ind, String sProp, String sPropval)
	{
		OWLObjectProperty property = factory.getOWLObjectProperty(IRI.create((baseString + sProp)));
		OWLIndividual indIndividual = factory.getOWLNamedIndividual(IRI.create(baseString + ind));
		OWLIndividual indPropVal = factory.getOWLNamedIndividual(IRI.create(baseString + sPropval));

		OWLObjectPropertyAssertionAxiom assertion = factory.getOWLObjectPropertyAssertionAxiom(property, indIndividual, indPropVal);
		AddAxiom addAxiomChange = new AddAxiom(ontology, assertion);
		return addAxiomChange;
	}

	public void SaveOntology(String filename)
	{
		File ff = new File(filename);
		try
		{
			manager.saveOntology(ontology, IRI.create(ff));
		} catch (OWLOntologyStorageException e)
		{
			e.printStackTrace();
		}
	}

	public void persistAxioms(ArrayList<AddAxiom> axs)
	{
		manager.applyChanges(axs);
	}

	public void PrintAndSave(String str)
	{
		if (SaverName != null)
		{
			BufferedWriter bufferedWriter = null;
			try
			{
				
				bufferedWriter = new BufferedWriter(new FileWriter(SaverName, true));
				
				bufferedWriter.write(str);
				bufferedWriter.close();
			} catch (IOException ex)
			{
				ex.printStackTrace();
			}
		}
		System.out.print(str);
	}

	public void PrintAndSaveln(String str)
	{
		PrintAndSave(str + "\n");
	}

	

	public void SaveInference(String str)
	{

		hermit.precomputeInferences();

		ArrayList<InferredAxiomGenerator<? extends OWLAxiom>> gens = new ArrayList<InferredAxiomGenerator<? extends OWLAxiom>>();

		try
		{
			gens.add(new InferredSubClassAxiomGenerator());

			OWLOntology infOnt;

			infOnt = manager.createOntology();

			InferredOntologyGenerator iog = new InferredOntologyGenerator(hermit, gens);

			iog.fillOntology(manager, infOnt);

			File ff = new File(str);

			manager.saveOntology(infOnt, IRI.create(ff));
		} catch (OWLOntologyCreationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OWLOntologyStorageException e)
		{
			e.printStackTrace();
		}

	}

	public void computeInference()
	{
		hermit.precomputeInferences();
	}

	public AddAxiom SetSubClassAxiom(String subclass, String ind)
	{
		OWLIndividual indIndividual = factory.getOWLNamedIndividual(IRI.create(baseString + ind));

		OWLClass subclassType = factory.getOWLClass(IRI.create(subclass));

		AddAxiom ax = new AddAxiom(ontology, factory.getOWLClassAssertionAxiom(subclassType, indIndividual));

		return ax;
	}

	public void removeIndividual(String strIndividual)
	{
		OWLEntityRemover remover = new OWLEntityRemover(manager, Collections.singleton(ontology));
		OWLNamedIndividual ind = factory.getOWLNamedIndividual(IRI.create(baseString + strIndividual));

		ind.accept(remover);
		manager.applyChanges(remover.getChanges());
	}

	
}
