package com.planet_ink.coffee_mud.utils;
import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;

import java.util.*;
import java.io.*;

/* 
   Copyright 2000-2004 Bo Zimmerman

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
public class MoneyUtils
{
	private MoneyUtils(){};
	public static Coins makeNote(int value, Environmental owner, Item container)
	{
		Coins msliver=null;
		if(value>=10000000)
		{
			msliver=(Coins)CMClass.getItem("GenCoins");
			msliver.setMaterial(EnvResource.RESOURCE_PAPER);
			msliver.setNumberOfCoins(10000000);
			msliver.setName("an Archons note");
			msliver.setDisplayText("a small crumpled note lies on the ground");
			msliver.setDescription("This note convertable to 10,000,000 gold coins.");
		}
		else
		if(value>=1000000)
		{
			msliver=(Coins)CMClass.getItem("GenCoins");
			msliver.setMaterial(EnvResource.RESOURCE_PAPER);
			msliver.setNumberOfCoins(1000000);
			msliver.setName("a Legends note");
			msliver.setDisplayText("a small crumpled note lies on the ground");
			msliver.setDescription("This note convertable to 1,000,000 gold coins.");
		}
		else
		if(value>=100000)
		{
			msliver=(Coins)CMClass.getItem("GenCoins");
			msliver.setMaterial(EnvResource.RESOURCE_PAPER);
			msliver.setNumberOfCoins(100000);
			msliver.setName("a Heroes note");
			msliver.setDisplayText("a small crumpled note lies on the ground");
			msliver.setDescription("This note convertable to 100,000 gold coins.");
		}
		else
		if(value>=10000)
		{
			msliver=(Coins)CMClass.getItem("GenCoins");
			msliver.setMaterial(EnvResource.RESOURCE_PAPER);
			msliver.setNumberOfCoins(10000);
			msliver.setName("a whole note");
			msliver.setDisplayText("a small crumpled note lies on the ground");
			msliver.setDescription("This note convertable to 10,000 gold coins.");
		}
		else
		if(value>=5000)
		{
			msliver=(Coins)CMClass.getItem("GenCoins");
			msliver.setMaterial(EnvResource.RESOURCE_PAPER);
			msliver.setNumberOfCoins(5000);
			msliver.setName("a half note");
			msliver.setDisplayText("a small crumpled note lies on the ground");
			msliver.setDescription("This note convertable to 5,000 gold coins.");
		}
		else
		if(value>=1000)
		{
			msliver=(Coins)CMClass.getItem("GenCoins");
			msliver.setMaterial(EnvResource.RESOURCE_PAPER);
			msliver.setNumberOfCoins(1000);
			msliver.setName("a adamantium note");
			msliver.setDisplayText("a small crumpled note lies on the ground");
			msliver.setDescription("This note convertable to 1000 gold coins.");
		}
		else
		if(value>=500)
		{
			msliver=(Coins)CMClass.getItem("GenCoins");
			msliver.setMaterial(EnvResource.RESOURCE_PAPER);
			msliver.setNumberOfCoins(500);
			msliver.setName("a mithril note");
			msliver.setDisplayText("a small crumpled note lies on the ground");
			msliver.setDescription("This note convertable to 500 gold coins.");
		}
		else
		if(value>=100)
		{
			msliver=(Coins)CMClass.getItem("GenCoins");
			msliver.setMaterial(EnvResource.RESOURCE_PAPER);
			msliver.setNumberOfCoins(100);
			msliver.setName("a platinum note");
			msliver.setDisplayText("a small crumpled note lies on the ground");
			msliver.setDescription("This note convertable to 100 gold coins.");
		}
		else
		if(value>=50)
		{
			msliver=(Coins)CMClass.getItem("GenCoins");
			msliver.setMaterial(EnvResource.RESOURCE_PAPER);
			msliver.setNumberOfCoins(50);
			msliver.setName("a golden note");
			msliver.setDisplayText("a small crumpled note lies on the ground");
			msliver.setDescription("This note convertable to 50 gold coins.");
		}
		else
		if(value>=10)
		{
			msliver=(Coins)CMClass.getItem("GenCoins");
			msliver.setMaterial(EnvResource.RESOURCE_PAPER);
			msliver.setNumberOfCoins(10);
			msliver.setName("a gleaming silver note");
			msliver.setDisplayText("a small crumpled note lies on the ground");
			msliver.setDescription("This note convertable to 10 gold coins.");
		}
		else
		if(value>=5)
		{
			msliver=(Coins)CMClass.getItem("GenCoins");
			msliver.setMaterial(EnvResource.RESOURCE_PAPER);
			msliver.setNumberOfCoins(5);
			msliver.setName("a fiver note");
			msliver.setDisplayText("a small crumpled note lies on the ground");
			msliver.setDescription("This note convertable to 5 gold coins.");
		}
		else
		if(value>0)
		{
			msliver=(Coins)CMClass.getItem("StdCoins");
			msliver.setNumberOfCoins(value);
		}
		else
		{
			msliver=(Coins)CMClass.getItem("StdCoins");
			msliver.setNumberOfCoins(1);
		}
		if(owner!=null)
		if(owner instanceof MOB)
			((MOB)owner).addInventory(msliver);
		else
		if(owner instanceof Room)
			((Room)owner).addItem(msliver);
		msliver.setContainer(container);
		msliver.recoverEnvStats();
		return msliver;
	}

	public static Item giveMoney(MOB banker, MOB customer, int value)
	{
		if(banker==null) banker=customer;
		Container changeBag=(Container)CMClass.getItem("GenContainer");
		changeBag.setCapacity(0);
		changeBag.baseEnvStats().setWeight(1);
		changeBag.setBaseValue(0);
		changeBag.setLidsNLocks(false,true,false,false);
		changeBag.setName("a change bag");
		changeBag.setDisplayText("a small crumpled bag lies here.");
		changeBag.setMaterial(EnvResource.RESOURCE_COTTON);
		changeBag.setContainTypes(Container.CONTAIN_COINS);
		changeBag.setDescription("");
		banker.addInventory(changeBag);

		int totalWeight=0;
		while(value>=10000000)
		{
			makeNote(value,banker,changeBag);
			value-=10000000;
			totalWeight++;
		}
		while(value>=1000000)
		{
			makeNote(value,banker,changeBag);
			value-=1000000;
			totalWeight++;
		}
		while(value>=100000)
		{
			makeNote(value,banker,changeBag);
			value-=100000;
			totalWeight++;
		}
		while(value>=10000)
		{
			makeNote(value,banker,changeBag);
			value-=10000;
			totalWeight++;
		}
		while(value>=5000)
		{
			makeNote(value,banker,changeBag);
			value-=5000;
			totalWeight++;
		}
		while(value>=1000)
		{
			makeNote(value,banker,changeBag);
			value-=1000;
			totalWeight++;
		}
		while(value>=500)
		{
			makeNote(value,banker,changeBag);
			value-=500;
			totalWeight++;
		}
		while(value>=100)
		{
			makeNote(value,banker,changeBag);
			value-=100;
			totalWeight++;
		}
		while(value>=50)
		{
			makeNote(value,banker,changeBag);
			value-=50;
			totalWeight++;
		}
		while(value>=10)
		{
			makeNote(value,banker,changeBag);
			value-=10;
			totalWeight++;
		}
		while(value>=5)
		{
			makeNote(value,banker,changeBag);
			value-=5;
			totalWeight++;
		}
		if(value>0)
		{
			makeNote(value,banker,changeBag);
			totalWeight+=value;
		}
		changeBag.setCapacity(totalWeight);
		changeBag.recoverEnvStats();
		changeBag.text();
		if(banker!=customer)
		{
			FullMsg newMsg=new FullMsg(banker,customer,changeBag,CMMsg.MSG_GIVE,"<S-NAME> give(s) <O-NAME> to <T-NAMESELF>.");
			if(banker.location().okMessage(banker,newMsg))
				banker.location().send(banker,newMsg);
			else
			{
				CommonMsgs.drop(banker,changeBag,true,false);
				return null;
			}
		}

		if(customer.isMine(changeBag))
		{
			Vector V=changeBag.getContents();
			if(V.size()>0)
			for(int i=0;i<customer.inventorySize();i++)
			{
				Item I=customer.fetchInventory(i);
				if((I!=null)
				&&(I!=changeBag)
				&&(I instanceof Container)
				&&(((Container)I).isOpen())
				&&(I.Name().equals(changeBag.Name())))
				{
					for(int v=0;v<V.size();v++)
						((Item)V.elementAt(v)).setContainer(I);
					changeBag.destroy();
					changeBag=(Container)I;
					break;
				}
			}
		}
		else
			CommonMsgs.drop(banker,changeBag,true,false);
		return changeBag;
	}

	public static void bankLedger(String bankName, String owner, String explanation)
	{
		Vector V=CMClass.DBEngine().DBReadData(owner,"LEDGER-"+bankName,"LEDGER-"+bankName+"/"+owner);
		if((V!=null)&&(V.size()>0))
		{
			Vector D=(Vector)V.firstElement();
			String last=(String)D.elementAt(3);
			if(last.length()>4096)
			{
			    int x=last.indexOf(";|;",1024);
			    if(x>=0) last=last.substring(x+3);
			}
			CMClass.DBEngine().DBDeleteData(owner,(String)D.elementAt(1),(String)D.elementAt(2));
			CMClass.DBEngine().DBCreateData(owner,(String)D.elementAt(1),(String)D.elementAt(2),last+explanation+";|;");
		}
		else
			CMClass.DBEngine().DBCreateData(owner,"LEDGER-"+bankName,"LEDGER-"+bankName+"/"+owner,explanation+";|;");
	}
	
	public static boolean modifyBankGold(String bankName, 
	        							 String owner,
	        							 String explanation,
	        							 int amount)
	{
		Vector V=CMClass.DBEngine().DBReadAllPlayerData(owner);
		for(int v=0;v<V.size();v++)
		{
			Vector D=(Vector)V.elementAt(v);
			String last=(String)D.elementAt(3);
			if(last.startsWith("COINS;"))
			{
				if((bankName==null)||(bankName.length()==0)||(bankName.equals(D.elementAt(1))))
				{
					Item I=CMClass.getItem("StdCoins");
					CoffeeMaker.setPropertiesStr(I,last.substring(6),true);
					I.recoverEnvStats();
					I.text();
					if((amount>0)||(((Coins)I).numberOfCoins()>=(-amount)))
					{
						((Coins)I).setNumberOfCoins(((Coins)I).numberOfCoins()+amount);
						CMClass.DBEngine().DBDeleteData(owner,(String)D.elementAt(1),(String)D.elementAt(2));
						CMClass.DBEngine().DBCreateData(owner,(String)D.elementAt(1),""+I+Math.random(),"COINS;"+CoffeeMaker.getPropertiesStr(I,true));
						bankLedger(bankName,owner,explanation);
						return true;
					}
				}
			}
		}
		return false;
	}

	public static boolean modifyThisAreaBankGold(Area A, 
	        									 HashSet triedBanks, 
	        									 String owner,
	        									 String explanation,
	        									 int amount)
	{
		Room R=null;
		for(Enumeration e=A.getMetroMap();e.hasMoreElements();)
		{
			R=(Room)e.nextElement();
			for(int m=0;m<R.numInhabitants();m++)
			{
				MOB M=R.fetchInhabitant(m);
				if((M instanceof Banker)&&(!triedBanks.contains(((Banker)M).bankChain())))
				{
					if(modifyBankGold(((Banker)M).bankChain(),owner,explanation,amount))
						return true;
					triedBanks.add(((Banker)M).bankChain());
				}
			}
		}
		return false;
	}
	
	public static boolean modifyLocalBankGold(Area A, 
	        								  String owner,
	        								  String explanation,
	        								  int amount)
	{
		HashSet triedBanks=new HashSet();
		if(modifyThisAreaBankGold(A,triedBanks,owner,explanation,amount))
			return true;
		for(Enumeration e=A.getParents();e.hasMoreElements();)
		{
			Area A2=(Area)e.nextElement();
			if(modifyThisAreaBankGold(A2,triedBanks,owner,explanation,amount))
				return true;
		}
		return modifyBankGold(null,owner,explanation,amount);
	}

	public static void subtractMoney(MOB banker, MOB mob, int amount)
	{
		if(mob==null) return;
		if(mob.getMoney()>=amount)
		{
			mob.setMoney(mob.getMoney()-amount);
			mob.recoverEnvStats();
			return;
		}
		Vector coinsRequired=new Vector();
		for(int i=0;i<mob.inventorySize();i++)
		{
			if(amount<=0) break;
			Item I=mob.fetchInventory(i);
			if((I!=null)&&(I instanceof Coins))
			{
				if(I.container()==null)
				{
					amount-=((Coins)I).numberOfCoins();
					coinsRequired.addElement(I);
				}
				else
				if((I.container().container()==null)
				&&(I.container() instanceof Container)
				&&(((Container)I.container()).isOpen()))
				{
					amount-=((Coins)I).numberOfCoins();
					coinsRequired.addElement(I);
				}
			}
		}
		for(int v=0;v<coinsRequired.size();v++)
			((Item)coinsRequired.elementAt(v)).destroy();
		if((amount>0)&&(mob.getMoney()>=amount))
		{
			mob.setMoney(mob.getMoney()-amount);
			mob.recoverEnvStats();
			return;
		}
		if(amount<0)
			giveMoney(banker,mob,-amount);
	}

	public static int totalMoney(MOB mob)
	{
		if(mob==null) return 0;
		int money=mob.getMoney();
		for(int i=0;i<mob.inventorySize();i++)
		{
			Item I=mob.fetchInventory(i);
			if((I!=null)&&(I instanceof Coins))
			{
				if(I.container()==null)
					money+=((Coins)I).numberOfCoins();
				else
				if((I.container().container()==null)
				&&(I.container() instanceof Container)
				&&(((Container)I.container()).isOpen()))
					money+=((Coins)I).numberOfCoins();
			}
		}
		return money;
	}
}
