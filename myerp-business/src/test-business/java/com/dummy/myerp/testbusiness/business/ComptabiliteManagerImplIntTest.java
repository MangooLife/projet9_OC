package com.dummy.myerp.testbusiness.business;

import com.dummy.myerp.business.contrat.manager.ComptabiliteManager;
import com.dummy.myerp.model.bean.comptabilite.*;
import com.dummy.myerp.technical.exception.FunctionalException;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

public class ComptabiliteManagerImplIntTest extends BusinessTestCase {

    private static ComptabiliteManager managerTestCase;
    private static EcritureComptable vEcritureComptable;
    private static List<SequenceEcritureComptable> listSeqExpected = new ArrayList<>();
    private static SequenceEcritureComptable sequenceEcritureComptable;
    private static SequenceEcritureComptable sequenceEcritureComptable2;
    private static Date dateNow;
    private static Calendar calendar;

    @Mock
    public ComptabiliteManager managerMock = mock(ComptabiliteManager.class);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();


    @BeforeClass
    public static void setUpBC() {
        managerTestCase = getBusinessProxy().getComptabiliteManager();
        dateNow = new Date();
        calendar = Calendar.getInstance();
        calendar.setTime(dateNow);
    }

    @Before
    public void setUp() {
        //GIVEN
        vEcritureComptable = new EcritureComptable();
        sequenceEcritureComptable = new SequenceEcritureComptable("AC", 2020, 1);
        listSeqExpected.add(sequenceEcritureComptable);
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat",listSeqExpected));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(123),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                null, null,
                new BigDecimal(123)));

        sequenceEcritureComptable2 = new SequenceEcritureComptable();
    }


    /*-- UNIT TEST : addReference --*/
    @Test
    @Transactional
    public void checkAddReferenceIfAlreadyInDB() throws Exception {
        //GIVEN
        sequenceEcritureComptable = new SequenceEcritureComptable("AC", 2016, 40);
        listSeqExpected.add(sequenceEcritureComptable);
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat",listSeqExpected));
        Calendar calendar = Calendar.getInstance();
        calendar.set(2016, 01, 01, 10, 59, 59);
        Date newDate = calendar.getTime();
        vEcritureComptable.setDate(newDate);
        vEcritureComptable.setReference("AC-2016/00040");
        //WHEN
        managerTestCase.addReference(vEcritureComptable);
        //THEN
        Assert.assertEquals(vEcritureComptable.getReference(), "AC-2016/00041");
    }

    @Test
    @Transactional
    public void checkAddReferenceIsNotInDB() throws Exception {
        //GIVEN
        EcritureComptable vEcritureComptable2 = new EcritureComptable();
        vEcritureComptable2.setJournal(new JournalComptable("OD", "Opérations Diverses"));
        vEcritureComptable2.setDate(dateNow);
        //WHEN
        managerTestCase.addReference(vEcritureComptable2);
        //THEN
        Assert.assertEquals(vEcritureComptable2.getReference(), "OD-2020/00001");
    }

    /*-- UNIT TEST : checkEcritureComptable --*/
    @Test
    @Transactional
    public void checkEcritureComptable() throws Exception {
        //GIVEN
        vEcritureComptable.setReference("AC-2020/00001");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1),
                null, new BigDecimal(123),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                null, null,
                new BigDecimal(123)));
        managerTestCase.checkEcritureComptable(vEcritureComptable);
    }

    @Test
    @Transactional
    public void checkEcritureComptableIfEcritureCExist() throws Exception {
        //GIVEN
        Calendar calendar = Calendar.getInstance();
        calendar.set(2016, 01, 01, 10, 59, 59);
        Date happyNewYearDate = calendar.getTime();
        vEcritureComptable.setDate(happyNewYearDate);
        vEcritureComptable.setReference("AC-2016/00001");
        //THEN
        expectedException.expect(FunctionalException.class);
        expectedException.expectMessage(
                "Une autre écriture comptable existe déjà avec la même référence.");
        //WHEN
        managerTestCase.checkEcritureComptable(vEcritureComptable);
    }

    /*-- UNIT TEST : insertEcritureComptable, update and delete--*/
    @Test
    @Transactional
    public void insertEcritureComptable() throws FunctionalException {
        //GIVEN
        EcritureComptable vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.setReference("AC-2020/00001");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(411),
                null, new BigDecimal(123),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(411),
                null, null,
                new BigDecimal(123)));
        //WHEN
        managerTestCase.insertEcritureComptable(vEcritureComptable);
        //THEN
        Assert.assertEquals(vEcritureComptable.getLibelle(), "Libelle");
        Assert.assertEquals(vEcritureComptable.getReference(), "AC-2020/00001");
        managerTestCase.deleteEcritureComptable(vEcritureComptable.getId());
    }

    @Test
    @Transactional
    public void updateEcritureComptable() throws FunctionalException {
        //GIVEN
        EcritureComptable vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        vEcritureComptable.setDate(new Date());
        vEcritureComptable.setLibelle("Libelle");
        vEcritureComptable.setReference("AC-2020/00001");
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(411),
                null, new BigDecimal(123),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(411),
                null, null,
                new BigDecimal(123)));
        managerTestCase.insertEcritureComptable(vEcritureComptable);
        //WHEN
        vEcritureComptable.setLibelle("Libelle2");
        vEcritureComptable.setReference("AC-2020/00002");
        managerTestCase.updateEcritureComptable(vEcritureComptable);
        //THEN
        Assert.assertEquals(vEcritureComptable.getLibelle(), "Libelle2");
        Assert.assertEquals(vEcritureComptable.getReference(), "AC-2020/00002");
        managerTestCase.deleteEcritureComptable(vEcritureComptable.getId());
    }

    @Test
    @Transactional
    public void deleteEcritureComptable() throws FunctionalException {
        managerTestCase.deleteEcritureComptable(vEcritureComptable.getId());
    }

    /*-- UNIT TEST : insertSequenceEcritureComptable update and delete--*/
    @Test
    @Transactional
    public void insertSequenceEcritureComptable() throws FunctionalException {
        //GIVEN
        sequenceEcritureComptable2.setJournalCode("AC");
        sequenceEcritureComptable2.setDerniereValeur(1);
        sequenceEcritureComptable2.setAnnee(2022);
        //WHEN
        managerTestCase.insertSequenceEcritureComptable(sequenceEcritureComptable2);
        int lastNumber = sequenceEcritureComptable2.getDerniereValeur();
        int annee = sequenceEcritureComptable2.getAnnee();
        //THEN
        Assert.assertEquals(sequenceEcritureComptable2.getJournalCode(), "AC");
        Assert.assertEquals(lastNumber, 1);
        Assert.assertEquals(annee, 2022);
        managerTestCase.deleteSequenceEcritureComptable(sequenceEcritureComptable2);
    }

    /*-- UNIT TEST : updateSequenceEcritureComptable --*/
    @Test
    @Transactional
    public void updateSequenceEcritureComptableTest() throws FunctionalException {
        //GIVEN
        sequenceEcritureComptable2.setJournalCode("VE");
        sequenceEcritureComptable2.setDerniereValeur(1);
        sequenceEcritureComptable2.setAnnee(2020);
        managerTestCase.insertSequenceEcritureComptable(sequenceEcritureComptable2);
        //WHEN
        sequenceEcritureComptable2.setDerniereValeur(2);
        managerTestCase.updateSequenceEcritureComptable(sequenceEcritureComptable2);
        int lastNumber = sequenceEcritureComptable2.getDerniereValeur();
        int annee = sequenceEcritureComptable2.getAnnee();
        //THEN
        Assert.assertEquals(sequenceEcritureComptable2.getJournalCode(), "VE");
        Assert.assertEquals(lastNumber, 2);
        Assert.assertEquals(annee, 2020);
        managerTestCase.deleteSequenceEcritureComptable(sequenceEcritureComptable2);
    }

    /*-- UNIT TEST : deleteSequenceEcritureComptable --*/
    @Test
    @Transactional
    public void deleteSequenceEcritureComptable() throws FunctionalException {
        //GIVEN
        sequenceEcritureComptable2.setJournalCode("AC");
        sequenceEcritureComptable2.setDerniereValeur(2);
        sequenceEcritureComptable2.setAnnee(2021);
        managerMock.insertSequenceEcritureComptable(sequenceEcritureComptable2);
        //WHEN
        managerMock.deleteSequenceEcritureComptable(sequenceEcritureComptable2);
        //THEN
        Mockito.verify(managerMock, times(1)).deleteSequenceEcritureComptable(sequenceEcritureComptable2);
    }

}

