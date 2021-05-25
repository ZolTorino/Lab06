package it.polito.tdp.meteo.model;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.meteo.DAO.MeteoDAO;

public class Model {
	
	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;

	MeteoDAO meteoDao;
	public Model() {
		meteoDao = new MeteoDAO();
	}

	// of course you can change the String output with what you think works best
	public List<Rilevamento> pulisci(List<Rilevamento> in)
	{
		LocalDate date = LocalDate.of(2013, 1, 1);
		while(date.getYear()==2013)
		{
			int check=0;
			for( int i=0;i<in.size();i++)
			{
				System.out.println(in.get(i).getData());
				System.out.println(date);
				if(in.get(i).getData().equals(date))
				{
					
					if(in.get(i).getLocalita().equals("Torino"))
						check++;
					if(in.get(i).getLocalita().equals("Milano"))
						check++;
					if(in.get(i).getLocalita().equals("Genova"))
					{
						check++;
					}
					
				}
			}
			if (check<3)
			{
				for( int i=0;i<in.size();i++)
				{
					
					if(in.get(i).getData()==date)
					{
						
						in.remove(i);
					}
				}
			}
			date=date.plusDays(1);
		}
		return in;
	}
	public String getUmiditaMedia(int mese) {
		
		List<Rilevamento> rilevamenti = new ArrayList<Rilevamento>(meteoDao.getAllRilevamenti());
		rilevamenti=pulisci(rilevamenti);
		int counter=0;
		int check=0;
		double milano=0, torino=0, genova=0;
		for( Rilevamento r: rilevamenti)
		{
			
			if(r.getData().getMonthValue()==mese)
			{
				check++;
				System.out.println( r.getLocalita());
				
				if(r.getLocalita().equals("Torino"))
					torino+=r.getUmidita();
				if(r.getLocalita().equals("Milano"))
					milano+=r.getUmidita();
				if(r.getLocalita().equals("Genova"))
				{
					genova+=r.getUmidita();
					
				}
				
			}
		}
		
		milano=milano/counter;
		torino=torino/counter;
		genova=genova/counter;
	
		return "Giorni del mese: "+check+ " "+counter+ " Torino: "+torino+" Milano: "+milano+" Genova: "+genova;
	}
	
	public List<List<Rilevamento>> rilevamenti15(int mese)
	{
		List<Rilevamento> rilevamenti = new ArrayList<Rilevamento>(meteoDao.getAllRilevamenti());
		List<List<Rilevamento>> out = new ArrayList<List<Rilevamento>>();
		
		int count=0;
		for( Rilevamento r: rilevamenti)
		{
			if(r.getData().getMonthValue()==mese&&count<15)
			{
				if(out.size()<15)
					out.add(new ArrayList<Rilevamento>());
				if(r.getLocalita().equals("Torino"))
				{
					out.get(count).add(r);
				}
				if(r.getLocalita().equals("Milano"))
					out.get(count).add(r);
				if(r.getLocalita().equals("Genova"))
					out.get(count).add(r); 
				if(out.get(count).size()==3)
					count++;
				
				}
		}
		
		return out;
	}
	// of course you can change the String output with what you think works best
	List<Rilevamento> risultato=new ArrayList<Rilevamento>();
	double minimo=100*100;
	//String ritorno="";
	List<Rilevamento> parzialecitta=new ArrayList<Rilevamento>();
	public String trovaSequenza(int mese) { 
		risultato=new ArrayList<Rilevamento>();
		minimo=100*100;
		parzialecitta=new ArrayList<Rilevamento>();
		calcola(0,rilevamenti15(mese),parzialecitta);
		String ritorno="";
		System.out.println( "Risultato size"+risultato.size());
		for(int i=0;i<risultato.size();i++)
		{
			ritorno+=risultato.get(i).getLocalita()+"\n";
		}
		ritorno+="Minimo: "+minimo;
		return ritorno;
		/*
		if(lettere.length()==0)
			{
				if(anagrammaDao.isCorrect(parziale))
					risultato.get(1).add(parziale);
				else
					risultato.get(0).add(parziale);
				
				System.out.println(parziale);
			}
			else 
			{
				for(int pos=0;pos<lettere.length();pos++)
				{
					char tentativo = lettere.charAt(pos);
					
					String nuovaParziale=parziale+tentativo;
					String nuovaLettere=lettere.substring(0,pos)+lettere.substring(pos+1);//ricostruisco la stringa togliendone una
					
					//potrei gia controllare se nuovaParziale è prefisso valido di una parola esistente, es aqz no
					permuta(nuovaParziale, nuovaLettere,livello+1, risultato);
					
					//rimetti a posto parziale e lettere (backtracking)
					//serve se sporco variabili che mi servono per risalire
					
				}
			}
			*/
		
	}
	
	
	void calcola(int livello,List<List<Rilevamento>> giornate,List<Rilevamento> parziale)
	{
		//System.out.println(livello);
		if(livello==giornate.size())
		{
			if(minimo>calcolacosto(parziale)) {
				//System.out.println("il massimo: "+massimo);
;				minimo=calcolacosto(parziale);
				this.risultato=new ArrayList<>(parziale);// IMPORTANTISSIMO, NON SOLO L'UGUALE
				
				
			}
		}
		
		else {
			for(int i=0; i<3;i++)
			{
				parziale.add(giornate.get(livello).get(i));
				//System.out.println(giornate.get(livello).get(i).getLocalita());

				if(isValid(parziale))
				{
					calcola(livello+1, giornate, parziale);
				}
				parziale.remove(parziale.size()-1);
				
			}
		}
	}
	
	boolean isValid(List<Rilevamento> test)
	{
		double milano=0, torino=0, genova=0;
		String attuale=test.get(0).getLocalita();
		int count=1;
		for(int i=1;i<test.size();i++)
		{
			if(test.get(i).getLocalita().equals("Torino"))
				torino++;
			if(test.get(i).getLocalita().equals("Milano"))
				milano++;
			if(test.get(i).getLocalita().equals("Genova"))
				genova++;
			
			if(test.get(i).getLocalita().equals(attuale))
			{
				count++;
			}

			if(!test.get(i).getLocalita().equals(attuale)&&count<3)
			{
				return false;
			}
			if(!test.get(i).getLocalita().equals(attuale)&&count>=3)
			{
				attuale=test.get(i).getLocalita();
				count=1;
			}
			
		}
		if(torino>=6||milano>=6||genova>=6)
			return false;
		else
			return true;
		
	}
	
	public double calcolacosto(List<Rilevamento> in)
	{
		double totale=0;
		totale+=in.get(0).getUmidita();
		for(int i=1;i<in.size();i++)
		{
			if(in.get(i).getLocalita()!=in.get(i-1).getLocalita())
				totale+=100;
			totale+=in.get(i).getUmidita();
		}
		return totale;
	}
	

}
