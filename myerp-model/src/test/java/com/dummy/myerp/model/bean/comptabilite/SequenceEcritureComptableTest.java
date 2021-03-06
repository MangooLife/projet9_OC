package com.dummy.myerp.model.bean.comptabilite;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class SequenceEcritureComptableTest {

    private SequenceEcritureComptable sequenceEcritureComptable;

    @Before
    public void setUp() {
        sequenceEcritureComptable = new SequenceEcritureComptable();
        sequenceEcritureComptable.setJournalCode("HA");
        sequenceEcritureComptable.setAnnee(2019);
        sequenceEcritureComptable.setDerniereValeur(1000);
    }

    @Test
    public void testToString() {
        //with constructor
        sequenceEcritureComptable = new SequenceEcritureComptable("HA",2019, 1000);

        StringBuilder vStB = new StringBuilder("SequenceEcritureComptable");
        String vSEP = ", ";
        vStB.append("{")
                .append("annee=").append(sequenceEcritureComptable.getAnnee())
                .append("journalCode=").append(sequenceEcritureComptable.getJournalCode())
                .append(vSEP).append("derniereValeur=").append(sequenceEcritureComptable.getDerniereValeur())
                .append("}");
        Assert.assertEquals(vStB.toString(),sequenceEcritureComptable.toString());
    }
}