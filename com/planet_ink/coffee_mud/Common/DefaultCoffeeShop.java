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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;



import java.util.*;
import com.planet_ink.coffee_mud.Libraries.interfaces.*;

public class DefaultCoffeeShop implements CoffeeShop
{
    public String ID(){return "DefaultCoffeeShop";}
    public int compareTo(Object o){ return CMClass.classID(this).compareToIgnoreCase(CMClass.classID(o));}
    public CMObject copyOf()
    {
        try
        {
            Object O=this.clone();
            ((DefaultCoffeeShop)O).cloneFix(this);
            return (CMObject)O;
        }
        catch(CloneNotSupportedException e)
        {
            return new DefaultCoffeeShop();
        }
    }
    public CMObject newInstance(){try{return (CMObject)getClass().newInstance();}catch(Exception e){return new DefaultCoffeeShop();}}
    
    public Vector baseInventory=new Vector(); // for Only Inventory situations
    public DVector storeInventory=new DVector(3);
    
    public void cloneFix(DefaultCoffeeShop E)
    {
        storeInventory=new DVector(3);
        baseInventory=new Vector();
        Hashtable copyFix=new Hashtable();
        for(int i=0;i<E.storeInventory.size();i++)
        {
            Environmental I2=(Environmental)E.storeInventory.elementAt(i,1);
            Integer N=(Integer)E.storeInventory.elementAt(i,2);
            Integer P=(Integer)E.storeInventory.elementAt(i,3);
            if(I2!=null)
            {
                Environmental I3=(Environmental)I2.copyOf();
                copyFix.put(I2,I3);
                storeInventory.addElement(I3,N,P);
            }
        }
        for(int i=0;i<E.baseInventory.size();i++)
        {
            Environmental I2=(Environmental)E.baseInventory.elementAt(i);
            if(I2!=null)
            {
                Environmental I3=(Environmental)copyFix.get(I2);
                if(I3==null) I3=(Environmental)I2.copyOf();
                baseInventory.addElement(I3);
            }
        }
    }

    public boolean inBaseInventory(Environmental thisThang)
    {
        for(int x=0;x<baseInventory.size();x++)
        {
            Environmental E=(Environmental)baseInventory.elementAt(x);
            if((thisThang.isGeneric())&&(E.isGeneric()))
            {
                if(thisThang.Name().equals(E.Name()))
                    return true;
            }
            else
            if(CMClass.className(thisThang).equals(CMClass.className(E)))
                return true;
        }
        return false;
    }

    public Environmental addStoreInventory(Environmental thisThang, ShopKeeper shop)
    {
        return addStoreInventory(thisThang,1,-1,shop);
    }

    public int baseStockSize()
    {
        return baseInventory.size();
    }

    public int totalStockSize()
    {
        return storeInventory.size();
    }

    public void clearStoreInventory()
    {
        storeInventory.clear();
        baseInventory.clear();
    }

    public Vector getStoreInventory()
    {
        return (Vector)storeInventory.getDimensionVector(1).clone();
    }
    public Vector getBaseInventory()
    {
        return baseInventory;
    }

    public Environmental addStoreInventory(Environmental thisThang, 
                                           int number, 
                                           int price,
                                           ShopKeeper shop)
    {
        if(number<0) number=1;
        if((shop.whatIsSold()==ShopKeeper.DEAL_INVENTORYONLY)&&(!inBaseInventory(thisThang)))
            baseInventory.addElement(thisThang.copyOf());
        Environmental originalUncopiedThang=thisThang;
        if(thisThang instanceof InnKey)
        {
            Environmental copy=null;
            for(int v=0;v<number;v++)
            {
                copy=(Environmental)thisThang.copyOf();
                ((InnKey)copy).hangOnRack(shop);
                storeInventory.addElement(copy,new Integer(1),new Integer(-1));
            }
        }
        else
        {
            Environmental copy=null;
            thisThang=(Environmental)thisThang.copyOf();
            for(int e=0;e<storeInventory.size();e++)
            {
                copy=(Environmental)storeInventory.elementAt(e,1);
                if(copy.Name().equals(thisThang.Name()))
                {
                    Integer I=(Integer)storeInventory.elementAt(e,2);
                    storeInventory.setElementAt(e,2,new Integer(I.intValue()+number));
                    if(price>0) storeInventory.setElementAt(e,3,new Integer(price));
                    return copy;
                }
            }
            storeInventory.addElement(thisThang,new Integer(number),new Integer(price));
        }
        if(originalUncopiedThang instanceof Item)
            ((Item)originalUncopiedThang).destroy();
        return thisThang;
    }
    
    public int totalStockWeight()
    {
        Environmental E=null;
        int weight=0;
        for(int i=0;i<storeInventory.size();i++)
        {
            E=(Environmental)storeInventory.elementAt(i,1);
            Integer I=(Integer)storeInventory.elementAt(i,2);
            if(I==null)
                weight+=E.envStats().weight();
            else
                weight+=(E.envStats().weight()*I.intValue());
        }
        return weight;
    }
    public int totalStockSizeIncludingDuplicates()
    {
        int num=0;
        for(int i=0;i<storeInventory.size();i++)
        {
            Integer I=(Integer)storeInventory.elementAt(i,2);
            if(I==null) 
                num++;
            else
                num+=I.intValue();
        }
        return num;
    }

    public void delAllStoreInventory(Environmental thisThang, int whatISell)
    {
        if((whatISell==ShopKeeper.DEAL_INVENTORYONLY)&&(inBaseInventory(thisThang)))
        {
            for(int v=baseInventory.size()-1;v>=0;v--)
            {
                Environmental E=(Environmental)baseInventory.elementAt(v);
                if((thisThang.isGeneric())&&(E.isGeneric()))
                {
                    if(thisThang.Name().equals(E.Name()))
                        baseInventory.removeElement(E);
                }
                else
                if(thisThang.ID().equals(E.ID()))
                    baseInventory.removeElement(E);
            }
        }
        for(int v=storeInventory.size()-1;v>=0;v--)
        {
            Environmental E=(Environmental)storeInventory.elementAt(v,1);
            if((thisThang.isGeneric())&&(E.isGeneric()))
            {
                if(thisThang.Name().equals(E.Name()))
                    storeInventory.removeElementAt(v);
            }
            else
            if(thisThang.ID().equals(E.ID()))
                storeInventory.removeElementAt(v);
        }
    }
    
    public boolean doIHaveThisInStock(String name, MOB mob, int whatISell, Room startRoom)
    {
        Environmental item=CMLib.english().fetchEnvironmental(storeInventory.getDimensionVector(1),name,true);
        if(item==null)
            item=CMLib.english().fetchEnvironmental(storeInventory.getDimensionVector(1),name,false);
        if((item==null)
           &&(mob!=null)
           &&((whatISell==ShopKeeper.DEAL_LANDSELLER)||(whatISell==ShopKeeper.DEAL_CLANDSELLER)
              ||(whatISell==ShopKeeper.DEAL_SHIPSELLER)||(whatISell==ShopKeeper.DEAL_CSHIPSELLER)))
        {
            Vector titles=CMLib.coffeeShops().addRealEstateTitles(new Vector(),mob,whatISell,startRoom);
            item=CMLib.english().fetchEnvironmental(titles,name,true);
            if(item==null)
                item=CMLib.english().fetchEnvironmental(titles,name,false);
        }
        if(item!=null)
           return true;
        return false;
    }

    public int stockPrice(Environmental likeThis)
    {
        Environmental E=null;
        Integer I=null;
        for(int v=0;v<storeInventory.size();v++)
        {
            E=(Environmental)storeInventory.elementAt(v,1);
            I=(Integer)storeInventory.elementAt(v,3);
            if((likeThis.isGeneric())&&(E.isGeneric()))
            {
                if(E.Name().equals(likeThis.Name()))
                    return I.intValue();
            }
            else
            if(E.ID().equals(likeThis.ID()))
                return I.intValue();
        }
        return -1;
    }
    public int numberInStock(Environmental likeThis)
    {
        int num=0;
        Environmental E=null;
        Integer N=null;
        for(int v=0;v<storeInventory.size();v++)
        {
            E=(Environmental)storeInventory.elementAt(v,1);
            N=(Integer)storeInventory.elementAt(v,2);
            if((likeThis.isGeneric())&&(E.isGeneric()))
            {
                if(E.Name().equals(likeThis.Name()))
                    num+=N.intValue();
            }
            else
            if(E.ID().equals(likeThis.ID()))
                num+=N.intValue();
        }

        return num;
    }

    public Environmental getStock(String name, MOB mob, int whatISell, Room startRoom)
    {
        Environmental item=CMLib.english().fetchEnvironmental(storeInventory.getDimensionVector(1),name,true);
        if(item==null)
            item=CMLib.english().fetchEnvironmental(storeInventory.getDimensionVector(1),name,false);
        if((item==null)
        &&((whatISell==ShopKeeper.DEAL_LANDSELLER)||(whatISell==ShopKeeper.DEAL_CLANDSELLER)
           ||(whatISell==ShopKeeper.DEAL_SHIPSELLER)||(whatISell==ShopKeeper.DEAL_CSHIPSELLER))
        &&(mob!=null))
        {
            Vector titles=CMLib.coffeeShops().addRealEstateTitles(new Vector(),mob,whatISell,startRoom);
            item=CMLib.english().fetchEnvironmental(titles,name,true);
            if(item==null)
                item=CMLib.english().fetchEnvironmental(titles,name,false);
        }
        return item;
    }


    public Environmental removeStock(String name, MOB mob, int whatISell, Room startRoom)
    {
        Environmental item=getStock(name,mob,whatISell,startRoom);
        if(item!=null)
        {
            if(item instanceof Ability)
                return item;

            int index=storeInventory.indexOf(item);
            if(index>=0)
            {
                Integer possNum=(Integer)storeInventory.elementAt(index,2);
                int possValue=possNum.intValue();
                possValue--;
                if(possValue>=1)
                    storeInventory.setElementAt(index,2,new Integer(possValue));
                if(possValue>1)
                    item=(Environmental)item.copyOf();
                if(possValue<1)
                    storeInventory.removeElementAt(index);
            }
            else
                storeInventory.removeElement(item);
            item.baseEnvStats().setRejuv(0);
            item.envStats().setRejuv(0);
        }
        return item;
    }
    
    public void emptyAllShelves()
    {
        if(storeInventory!=null)storeInventory.clear();
        if(baseInventory!=null)baseInventory.clear();
    }
    public Vector removeSellableProduct(String named, MOB mob, int whatISell, Room startRoom)
    {
        Vector V=new Vector();
        Environmental product=removeStock(named,mob,whatISell,startRoom);
        if(product==null) return V;
        V.addElement(product);
        if(product instanceof Container)
        {
            int i=0;
            Key foundKey=null;
            Container C=((Container)product);
            while(i<storeInventory.size())
            {
                int a=storeInventory.size();
                Environmental I=(Environmental)storeInventory.elementAt(i,1);
                if((I instanceof Item)&&(((Item)I).container()==product))
                {
                    if((I instanceof Key)&&(((Key)I).getKey().equals(C.keyName())))
                        foundKey=(Key)I;
                    ((Item)I).unWear();
                    V.addElement(I);
                    storeInventory.removeElement(I);
                    ((Item)I).setContainer((Item)product);
                }
                if(a==storeInventory.size())
                    i++;
            }
            if((C.isLocked())&&(foundKey==null))
            {
                String keyName=Double.toString(Math.random());
                C.setKeyName(keyName);
                C.setLidsNLocks(C.hasALid(),true,C.hasALock(),false);
                Key key=(Key)CMClass.getItem("StdKey");
                key.setKey(keyName);
                key.setContainer(C);
                V.addElement(key);
            }
        }
        return V;
    }
    
    public String makeXML(ShopKeeper shop)
    {
        Vector V=getStoreInventory();
        if((V!=null)&&(V.size()>0))
        {
            StringBuffer itemstr=new StringBuffer("");
            itemstr.append(CMLib.xml().convertXMLtoTag("ISELL",shop.whatIsSold()));
            itemstr.append(CMLib.xml().convertXMLtoTag("IPREJ",shop.prejudiceFactors()));
            itemstr.append(CMLib.xml().convertXMLtoTag("IBUDJ",shop.budget()));
            itemstr.append(CMLib.xml().convertXMLtoTag("IDVAL",shop.devalueRate()));
            itemstr.append(CMLib.xml().convertXMLtoTag("IGNOR",shop.ignoreMask()));
            itemstr.append("<INVS>");
            for(int i=0;i<V.size();i++)
            {
                Item I=(Item)V.elementAt(i);
                itemstr.append("<INV>");
                itemstr.append(CMLib.xml().convertXMLtoTag("ICLASS",CMClass.className(I)));
                itemstr.append(CMLib.xml().convertXMLtoTag("INUM",""+numberInStock(I)));
                itemstr.append(CMLib.xml().convertXMLtoTag("IVAL",""+stockPrice(I)));
                itemstr.append(CMLib.xml().convertXMLtoTag("IDATA",CMLib.coffeeMaker().getPropertiesStr(I,true)));
                itemstr.append("</INV>");
            }
            return itemstr.toString()+"</INVS>";
        }
        return "";
    }

    public void buildShopFromXML(String text, ShopKeeper shop)
    {
        Vector V=new Vector();
        storeInventory=new DVector(3);
        baseInventory=new Vector();
        
        if(text.length()==0) return;
        if(!text.trim().startsWith("<"))
        {
            String parm=CMParms.getParmStr(text,"ISELL",""+ShopKeeper.DEAL_ANYTHING);
            if((parm!=null)&&(CMath.isNumber(parm))) 
                shop.setWhatIsSold(CMath.s_int(parm));
            else
            if(parm!=null)
            for(int s=0;s<ShopKeeper.DEAL_DESCS.length;s++)
                if(parm.equalsIgnoreCase(ShopKeeper.DEAL_DESCS[s]))
                    shop.setWhatIsSold(s);
            parm=CMParms.getParmStr(text,"IPREJ","");
            if(parm!=null) shop.setPrejudiceFactors(parm);
            parm=CMParms.getParmStr(text,"IBUDJ","1000000");
            if(parm!=null) shop.setBudget(parm);
            parm=CMParms.getParmStr(text,"IDVAL","");
            if(parm!=null) shop.setDevalueRate(parm);
            parm=CMParms.getParmStr(text,"IGNOR","");
            if(parm!=null) shop.setIgnoreMask(parm);
            return;
        }

        Vector buf=CMLib.xml().parseAllXML(text);
        if(buf==null)
        {
            Log.errOut("Merchant","Error parsing data.");
            return;
        }
        String parm=CMLib.xml().getValFromPieces(buf,"ISELL");
        if((parm!=null)&&(CMath.isNumber(parm))) 
            shop.setWhatIsSold(CMath.s_int(parm));
        parm=CMLib.xml().getValFromPieces(buf,"IPREJ");
        if(parm!=null) shop.setPrejudiceFactors(parm);
        parm=CMLib.xml().getValFromPieces(buf,"IBUDJ");
        if(parm!=null) shop.setBudget(parm);
        parm=CMLib.xml().getValFromPieces(buf,"IDVAL");
        if(parm!=null) shop.setDevalueRate(parm);
        parm=CMLib.xml().getValFromPieces(buf,"IGNOR");
        if(parm!=null) shop.setIgnoreMask(parm);
        
        Vector iV=CMLib.xml().getRealContentsFromPieces(buf,"INVS");
        if(iV==null)
        {
            Log.errOut("Merchant","Error parsing 'INVS'.");
            return;
        }
        for(int i=0;i<iV.size();i++)
        {
            XMLLibrary.XMLpiece iblk=(XMLLibrary.XMLpiece)iV.elementAt(i);
            if((!iblk.tag.equalsIgnoreCase("INV"))||(iblk.contents==null))
            {
                Log.errOut("Merchant","Error parsing 'INVS' data.");
                return;
            }
            String itemi=CMLib.xml().getValFromPieces(iblk.contents,"ICLASS");
            int itemnum=CMLib.xml().getIntFromPieces(iblk.contents,"INUM");
            int val=CMLib.xml().getIntFromPieces(iblk.contents,"IVAL");
            Environmental newOne=CMClass.getItem(itemi);
            Vector idat=CMLib.xml().getRealContentsFromPieces(iblk.contents,"IDATA");
            if((idat==null)||(newOne==null)||(!(newOne instanceof Item)))
            {
                Log.errOut("Merchant","Error parsing 'INV' data.");
                return;
            }
            CMLib.coffeeMaker().setPropertiesStr(newOne,idat,true);
            Item I=(Item)newOne;
            I.recoverEnvStats();
            V.addElement(I);
            addStoreInventory(I,itemnum,val,shop);
        }
    }
}
