package com.planet_ink.coffee_mud.Common;
import com.planet_ink.coffee_mud.core.interfaces.*;
import com.planet_ink.coffee_mud.core.*;
import com.planet_ink.coffee_mud.Abilities.interfaces.*;
import com.planet_ink.coffee_mud.Areas.interfaces.*;
import com.planet_ink.coffee_mud.Behaviors.interfaces.*;
import com.planet_ink.coffee_mud.CharClasses.interfaces.*;
import com.planet_ink.coffee_mud.Commands.interfaces.*;
import com.planet_ink.coffee_mud.Common.interfaces.*;
import com.planet_ink.coffee_mud.Exits.interfaces.*;
import com.planet_ink.coffee_mud.Items.interfaces.*;
import com.planet_ink.coffee_mud.Locales.interfaces.*;
import com.planet_ink.coffee_mud.MOBS.interfaces.*;
import com.planet_ink.coffee_mud.Races.interfaces.*;


import java.util.*;

/* 
   Copyright 2000-2006 Bo Zimmerman

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
public class DefaultTimeClock implements TimeClock
{
	public String ID(){return "DefaultTimeClock";}
	public String name(){return "Time Object";}
    public CMObject newInstance(){try{return (CMObject)getClass().newInstance();}catch(Exception e){return new DefaultTimeClock();}}
    
	protected long tickStatus=Tickable.STATUS_NOT;
	public long getTickStatus(){return tickStatus;}
	protected boolean loaded=false;
	protected String loadName=null;
	public void setLoadName(String name){loadName=name;}
	protected int year=1;
	protected int month=1;
	protected int day=1;
	protected int time=0;
	protected int hoursInDay=6;
    protected String[] monthsInYear={
			 "the 1st month","the 2nd month","the 3rd month","the 4th month",
			 "the 5th month","the 6th month","the 7th month","the 8th month"
	};
	protected int daysInMonth=20;
    protected int[] dawnToDusk={0,1,4,6};
    protected String[] weekNames={};
    protected String[] yearNames={"year #"};
	
	public int getHoursInDay(){return hoursInDay;}
	public void setHoursInDay(int h){hoursInDay=h;}
	public int getDaysInMonth(){return daysInMonth;}
	public void setDaysInMonth(int d){daysInMonth=d;}
	public int getMonthsInYear(){return monthsInYear.length;}
	public String[] getMonthNames(){return monthsInYear;}
	public void setMonthsInYear(String[] months){monthsInYear=months;}
	public int[] getDawnToDusk(){return dawnToDusk;}
	public String[] getYearNames(){return yearNames;}
	public void setYearNames(String[] years){yearNames=years;}
	public void setDawnToDusk(int dawn, int day, int dusk, int night)
	{ 
		dawnToDusk[TIME_DAWN]=dawn;
		dawnToDusk[TIME_DAY]=day;
		dawnToDusk[TIME_DUSK]=dusk;
		dawnToDusk[TIME_NIGHT]=night;
	}
	public String[] getWeekNames(){return weekNames;}
	public int getDaysInWeek(){return weekNames.length;}
	public void setDaysInWeek(String[] days){weekNames=days;}
	
    public String getShortestTimeDescription()
    {
        StringBuffer timeDesc=new StringBuffer("");
        timeDesc.append(getYear());
        timeDesc.append("/"+getMonth());
        timeDesc.append("/"+getDayOfMonth());
        timeDesc.append(" HR:"+getTimeOfDay());
        return timeDesc.toString();
    }
    public String getShortTimeDescription()
    {
        StringBuffer timeDesc=new StringBuffer("");
        timeDesc.append("hour "+getTimeOfDay()+" on ");
        if(getDaysInWeek()>0)
        {
            long x=((long)getYear())*((long)getMonthsInYear())*getDaysInMonth();
            x=x+((long)(getMonth()-1))*((long)getDaysInMonth());
            x=x+getDayOfMonth();
            timeDesc.append(getWeekNames()[(int)(x%getDaysInWeek())]+", ");
        }
        timeDesc.append("the "+getDayOfMonth()+numAppendage(getDayOfMonth()));
        timeDesc.append(" day of "+getMonthNames()[getMonth()-1]);
        if(getYearNames().length>0)
            timeDesc.append(", "+CMStrings.replaceAll(getYearNames()[getYear()%getYearNames().length],"#",""+getYear()));
        return timeDesc.toString();
    }
    
    public int determineSeason(String str)
    {
        str=str.toUpperCase().trim();
        if(str.length()==0) return -1;
        for(int i=0;i<TimeClock.SEASON_DESCS.length;i++)
            if(TimeClock.SEASON_DESCS[i].startsWith(str))
                return i;
        return -1;
    }
    
    public void initializeINIClock(CMProps page)
    {
        if(CMath.s_int(page.getStr("HOURSINDAY"))>0)
            setHoursInDay(CMath.s_int(page.getStr("HOURSINDAY")));

        if(CMath.s_int(page.getStr("DAYSINMONTH"))>0)
            setDaysInMonth(CMath.s_int(page.getStr("DAYSINMONTH")));

        String monthsInYear=page.getStr("MONTHSINYEAR");
        if(monthsInYear.trim().length()>0)
            setMonthsInYear(CMParms.toStringArray(CMParms.parseCommas(monthsInYear,true)));

        setDaysInWeek(CMParms.toStringArray(CMParms.parseCommas(page.getStr("DAYSINWEEK"),true)));

        if(page.containsKey("YEARDESC"))
            setYearNames(CMParms.toStringArray(CMParms.parseCommas(page.getStr("YEARDESC"),true)));

        if(page.containsKey("DAWNHR")&&page.containsKey("DAYHR")
                &&page.containsKey("DUSKHR")&&page.containsKey("NIGHTHR"))
        setDawnToDusk(
                        CMath.s_int(page.getStr("DAWNHR")),
                        CMath.s_int(page.getStr("DAYHR")),
                        CMath.s_int(page.getStr("DUSKHR")),
                        CMath.s_int(page.getStr("NIGHTHR")));

        CMProps.setIntVar(CMProps.SYSTEMI_TICKSPERMUDDAY,""+((MudHost.TIME_MILIS_PER_MUDHOUR*CMClass.globalClock().getHoursInDay()/Tickable.TIME_TICK)));
        CMProps.setIntVar(CMProps.SYSTEMI_TICKSPERMUDMONTH,""+((MudHost.TIME_MILIS_PER_MUDHOUR*CMClass.globalClock().getHoursInDay()*CMClass.globalClock().getDaysInMonth()/Tickable.TIME_TICK)));
    }
    
	public String timeDescription(MOB mob, Room room)
	{
		StringBuffer timeDesc=new StringBuffer("");

		if((CMLib.flags().canSee(mob))&&(getTODCode()>=0))
			timeDesc.append(TOD_DESC[getTODCode()]);
		timeDesc.append("(Hour: "+getTimeOfDay()+"/"+(getHoursInDay()-1)+")");
		timeDesc.append("\n\rIt is ");
		if(getDaysInWeek()>0)
		{
			long x=((long)getYear())*((long)getMonthsInYear())*getDaysInMonth();
			x=x+((long)(getMonth()-1))*((long)getDaysInMonth());
			x=x+getDayOfMonth();
			timeDesc.append(getWeekNames()[(int)(x%getDaysInWeek())]+", ");
		}
		timeDesc.append("the "+getDayOfMonth()+numAppendage(getDayOfMonth()));
		timeDesc.append(" day of "+getMonthNames()[getMonth()-1]);
		if(getYearNames().length>0)
			timeDesc.append(", "+CMStrings.replaceAll(getYearNames()[getYear()%getYearNames().length],"#",""+getYear()));
		timeDesc.append(".\n\rIt is "+(TimeClock.SEASON_DESCS[getSeasonCode()]).toLowerCase()+".");
		if((CMLib.flags().canSee(mob))
		&&(getTODCode()==TimeClock.TIME_NIGHT)
		&&(CMLib.utensils().hasASky(room)))
		{
			switch(room.getArea().getClimateObj().weatherType(room))
			{
			case Climate.WEATHER_BLIZZARD:
			case Climate.WEATHER_HAIL:
			case Climate.WEATHER_SLEET:
			case Climate.WEATHER_SNOW:
			case Climate.WEATHER_RAIN:
			case Climate.WEATHER_THUNDERSTORM:
				timeDesc.append("\n\r"+room.getArea().getClimateObj().weatherDescription(room)+" You can't see the moon."); break;
			case Climate.WEATHER_CLOUDY:
				timeDesc.append("\n\rThe clouds obscure the moon."); break;
			case Climate.WEATHER_DUSTSTORM:
				timeDesc.append("\n\rThe dust obscures the moon."); break;
			default:
				if(getMoonPhase()>=0)
					timeDesc.append("\n\r"+MOON_PHASES[getMoonPhase()]);
				break;
			}
		}
		return timeDesc.toString();
	}

	protected String numAppendage(int num)
	{
	    String strn=""+num;
        if((num<11)||(num>13))
		switch(CMath.s_int(""+(strn).charAt(strn.length()-1)))
		{
		case 1: return "st";
		case 2: return "nd";
		case 3: return "rd";
		}
		return "th";
	}

	public int getYear(){return year;}
	public void setYear(int y){year=y;}

	public int getSeasonCode(){
	    int div=(int)Math.round(Math.floor(CMath.div(getMonthsInYear(),4.0)));
	    if(month<div) return TimeClock.SEASON_WINTER;
	    if(month<(div*2)) return TimeClock.SEASON_SPRING;
	    if(month<(div*3)) return TimeClock.SEASON_SUMMER;
	    return TimeClock.SEASON_FALL;
	}
	public int getMonth(){return month;}
	public void setMonth(int m){month=m;}
	public int getMoonPhase(){return (int)Math.round(Math.floor(CMath.mul(CMath.div(getDayOfMonth(),getDaysInMonth()),8.0)));}

	public int getDayOfMonth(){return day;}
	public void setDayOfMonth(int d){day=d;}
	public int getTimeOfDay(){return time;}
	public int getTODCode()
	{
		if((time>=getDawnToDusk()[TimeClock.TIME_NIGHT])&&(getDawnToDusk()[TimeClock.TIME_NIGHT]>=0))
			return TimeClock.TIME_NIGHT;
		if((time>=getDawnToDusk()[TimeClock.TIME_DUSK])&&(getDawnToDusk()[TimeClock.TIME_DUSK]>=0))
			return TimeClock.TIME_DUSK;
		if((time>=getDawnToDusk()[TimeClock.TIME_DAY])&&(getDawnToDusk()[TimeClock.TIME_DAY]>=0))
			return TimeClock.TIME_DAY;
		if((time>=getDawnToDusk()[TimeClock.TIME_DAWN])&&(getDawnToDusk()[TimeClock.TIME_DAWN]>=0))
			return TimeClock.TIME_DAWN;
		return TimeClock.TIME_DAY;
	}
	public boolean setTimeOfDay(int t)
	{
		int oldCode=getTODCode();
		time=t;
		return getTODCode()!=oldCode;
	}
    
    public CMObject copyOf()
    {
        try
        {
            TimeClock C=(TimeClock)this.clone();
            return C;
        }
        catch(CloneNotSupportedException e)
        {
            return new DefaultTimeClock();
        }
    }
    public TimeClock deriveClock(long millis)
    {
        try
        {
            TimeClock C=(TimeClock)this.clone();
            long diff=(System.currentTimeMillis()-millis)/MudHost.TIME_MILIS_PER_MUDHOUR;
            C.tickTock((int)diff);
            return C;
        }
        catch(CloneNotSupportedException e)
        {
            
        }
        return CMClass.globalClock();
    }

    public long deriveMillisAfter(TimeClock C)
    {
        long numMudHours=0;
        if(C.getYear()>getYear()) return -1;
        else 
        if(C.getYear()==getYear())
            if(C.getMonth()>getMonth()) return -1;
            else 
            if(C.getMonth()==getMonth())
                if(C.getDayOfMonth()>getDayOfMonth()) return -1;
                else 
                if(C.getDayOfMonth()==getDayOfMonth())
                    if(C.getTimeOfDay()>getTimeOfDay()) return -1;
        numMudHours+=(getYear()-C.getYear())*(getHoursInDay()*getDaysInMonth()*getMonthsInYear());
        numMudHours+=(getMonth()-C.getMonth())*(getHoursInDay()*getDaysInMonth());
        numMudHours+=(getDayOfMonth()-C.getDayOfMonth())*getHoursInDay();
        numMudHours+=(getTimeOfDay()-C.getTimeOfDay());
        return numMudHours*MudHost.TIME_MILIS_PER_MUDHOUR;
    }
    
	public void raiseLowerTheSunEverywhere()
	{
	    try
	    {
			for(Enumeration r=CMLib.map().rooms();r.hasMoreElements();)
			{
				Room R=(Room)r.nextElement();
				if((R!=null)
				&&(R.getArea()!=null)
				&&(R.getArea().getTimeObj()==this)
				&&((R.numInhabitants()>0)||(R.numItems()>0)))
				{
					R.recoverEnvStats();
					for(int m=0;m<R.numInhabitants();m++)
					{
						MOB mob=R.fetchInhabitant(m);
						if((mob!=null)
						&&(!mob.isMonster()))
						{
							if(CMLib.utensils().hasASky(R)
							&&(!CMLib.flags().isSleeping(mob))
							&&(CMLib.flags().canSee(mob)))
							{
								switch(getTODCode())
								{
								case TimeClock.TIME_DAWN:
									mob.tell("^JThe sun begins to rise in the west.^?");
									break;
								case TimeClock.TIME_DAY:
									break;
									//mob.tell("The sun is now shining brightly."); break;
								case TimeClock.TIME_DUSK:
									mob.tell("^JThe sun begins to set in the east.^?"); break;
								case TimeClock.TIME_NIGHT:
									mob.tell("^JThe sun has set and darkness again covers the world.^?"); break;
								}
							}
							else
							{
								switch(getTODCode())
								{
								case TimeClock.TIME_DAWN:
									mob.tell("It is now daytime."); break;
								case TimeClock.TIME_DAY: break;
									//mob.tell("The sun is now shining brightly."); break;
								case TimeClock.TIME_DUSK: break;
									//mob.tell("It is almost nighttime."); break;
								case TimeClock.TIME_NIGHT:
									mob.tell("It is nighttime."); break;
								}
							}
						}
					}
				}
				R.recoverRoomStats();
			}
	    }catch(java.util.NoSuchElementException x){}
	}

	public void tickTock(int howManyHours)
	{
	    int todCode=getTODCode();
		if(howManyHours!=0)
		{
			setTimeOfDay(getTimeOfDay()+howManyHours);
			lastTicked=System.currentTimeMillis();
			while(getTimeOfDay()>=getHoursInDay())
			{
				setTimeOfDay(getTimeOfDay()-getHoursInDay());
				setDayOfMonth(getDayOfMonth()+1);
				if(getDayOfMonth()>getDaysInMonth())
				{
					setDayOfMonth(1);
					setMonth(getMonth()+1);
					if(getMonth()>getMonthsInYear())
					{
						setMonth(1);
						setYear(getYear()+1);
					}
				}
			}
			while(getTimeOfDay()<0)
			{
				setTimeOfDay(getHoursInDay()+getTimeOfDay());
				setDayOfMonth(getDayOfMonth()-1);
				if(getDayOfMonth()<1)
				{
					setDayOfMonth(getDaysInMonth());
					setMonth(getMonth()-1);
					if(getMonth()<1)
					{
						setMonth(getMonthsInYear());
						setYear(getYear()-1);
					}
				}
			}
		}
		if(getTODCode()!=todCode) raiseLowerTheSunEverywhere();
	}
	public void save()
	{
		if((loaded)&&(loadName!=null))
		{
			CMLib.database().DBDeleteData(loadName,"TIMECLOCK");
			CMLib.database().DBCreateData(loadName,"TIMECLOCK","TIMECLOCK/"+loadName,
			"<DAY>"+getDayOfMonth()+"</DAY><MONTH>"+getMonth()+"</MONTH><YEAR>"+getYear()+"</YEAR>"
			+"<HOURS>"+getHoursInDay()+"</HOURS><DAYS>"+getDaysInMonth()+"</DAYS>"
			+"<MONTHS>"+CMParms.toStringList(getMonthNames())+"</MONTHS>"
			+"<DAWNHR>"+getDawnToDusk()[TIME_DAWN]+"</DAWNHR>"
			+"<DAYHR>"+getDawnToDusk()[TIME_DAY]+"</DAYHR>"
			+"<DUSKHR>"+getDawnToDusk()[TIME_DUSK]+"</DUSKHR>"
			+"<NIGHTHR>"+getDawnToDusk()[TIME_NIGHT]+"</NIGHTHR>"
			+"<WEEK>"+CMParms.toStringList(getWeekNames())+"</WEEK>"
			+"<YEARS>"+CMParms.toStringList(getYearNames())+"</YEARS>"
			);
		}
	}

	public long lastTicked=0;
	public boolean tick(Tickable ticking, int tickID)
	{
		tickStatus=Tickable.STATUS_NOT;
		synchronized(this)
		{
			if((loadName!=null)&&(!loaded))
			{
				loaded=true;
				Vector V=CMLib.database().DBReadData(loadName,"TIMECLOCK");
				String timeRsc=null;
				if((V==null)||(V.size()==0)||(!(V.elementAt(0) instanceof Vector)))
					timeRsc="<TIME>-1</TIME><DAY>1</DAY><MONTH>1</MONTH><YEAR>1</YEAR>";
				else
					timeRsc=(String)((Vector)V.elementAt(0)).elementAt(3);
				V=CMLib.xml().parseAllXML(timeRsc);
				setTimeOfDay(CMLib.xml().getIntFromPieces(V,"TIME"));
				setDayOfMonth(CMLib.xml().getIntFromPieces(V,"DAY"));
				setMonth(CMLib.xml().getIntFromPieces(V,"MONTH"));
				setYear(CMLib.xml().getIntFromPieces(V,"YEAR"));
				if(this!=CMClass.globalClock())
				{
					if((CMLib.xml().getValFromPieces(V,"HOURS").length()==0)
					||(CMLib.xml().getValFromPieces(V,"DAYS").length()==0)
					||(CMLib.xml().getValFromPieces(V,"MONTHS").length()==0))
					{
						setHoursInDay(CMClass.globalClock().getHoursInDay());
						setDaysInMonth(CMClass.globalClock().getDaysInMonth());
						setMonthsInYear(CMClass.globalClock().getMonthNames());
						setDawnToDusk(CMClass.globalClock().getDawnToDusk()[TIME_DAWN],
                                      CMClass.globalClock().getDawnToDusk()[TIME_DAY],
                                      CMClass.globalClock().getDawnToDusk()[TIME_DUSK],
                                      CMClass.globalClock().getDawnToDusk()[TIME_NIGHT]);
						setDaysInWeek(CMClass.globalClock().getWeekNames());
						setYearNames(CMClass.globalClock().getYearNames());
					}
					else
					{
						setHoursInDay(CMLib.xml().getIntFromPieces(V,"HOURS"));
						setDaysInMonth(CMLib.xml().getIntFromPieces(V,"DAYS"));
						setMonthsInYear(CMParms.toStringArray(CMParms.parseCommas(CMLib.xml().getValFromPieces(V,"MONTHS"),true)));
						setDawnToDusk(CMLib.xml().getIntFromPieces(V,"DAWNHR"),
									  CMLib.xml().getIntFromPieces(V,"DAYHR"),
									  CMLib.xml().getIntFromPieces(V,"DUSKHR"),
									  CMLib.xml().getIntFromPieces(V,"NIGHTHR"));
						setDaysInWeek(CMParms.toStringArray(CMParms.parseCommas(CMLib.xml().getValFromPieces(V,"WEEK"),true)));
						setYearNames(CMParms.toStringArray(CMParms.parseCommas(CMLib.xml().getValFromPieces(V,"YEARS"),true)));
					}
				}
			}
			if((System.currentTimeMillis()-lastTicked)>MudHost.TIME_MILIS_PER_MUDHOUR)
				tickTock(1);
		}
		return true;
	}
    public int compareTo(Object o){ return CMClass.classID(this).compareToIgnoreCase(CMClass.classID(o));}
}
