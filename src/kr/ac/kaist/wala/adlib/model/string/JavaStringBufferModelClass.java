package kr.ac.kaist.wala.adlib.model.string;

import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.NewSiteReference;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.ipa.summaries.MethodSummary;
import com.ibm.wala.ipa.summaries.SummarizedMethod;
import com.ibm.wala.ipa.summaries.SummarizedMethodWithNames;
import com.ibm.wala.ipa.summaries.VolatileMethodSummary;
import com.ibm.wala.shrikeBT.IInvokeInstruction;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.types.*;
import com.ibm.wala.util.ssa.SSAValue;
import com.ibm.wala.util.ssa.TypeSafeInstructionFactory;
import kr.ac.kaist.wala.adlib.model.ModelClass;

import java.util.ArrayList;
import java.util.List;

/**
 * A modeling class for Android built-in android/os/Handler.
 * Created by leesh on 14/01/2017.
 */
public class JavaStringBufferModelClass extends ModelClass {

    public static final TypeReference JAVA_STRINGBUFFER_MODEL_CLASS = TypeReference.findOrCreate(
            ClassLoaderReference.Primordial, TypeName.string2TypeName("Ljava/lang/StringBuffer"));

    public static final Selector[] mSelectors = {
            Selector.make("<init>()V"),
            Selector.make("<init>(Ljava/lang/CharSequence;)V"),
            Selector.make("<init>(I)V"),
            Selector.make("<init>(Ljava/lang/String;)V"),
            Selector.make("append(B)Ljava/lang/StringBuffer;"),
            Selector.make("append(C)Ljava/lang/StringBuffer;"),
            Selector.make("append([C)Ljava/lang/StringBuffer;"),
            Selector.make("append([CII)Ljava/lang/StringBuffer;"),
            Selector.make("append(Ljava/lang/CharSequence;)Ljava/lang/StringBuffer;"),
            Selector.make("append(Ljava/lang/CharSequence;II)Ljava/lang/StringBuffer;"),
            Selector.make("append(D)Ljava/lang/StringBuffer;"),
            Selector.make("append(F)Ljava/lang/StringBuffer;"),
            Selector.make("append(I)Ljava/lang/StringBuffer;"),
            Selector.make("append(J)Ljava/lang/StringBuffer;"),
            Selector.make("append(Ljava/lang/Object;)Ljava/lang/StringBuffer;"),
            Selector.make("append(Ljava/lang/String;)Ljava/lang/StringBuffer;"),
            Selector.make("append(Ljava/lang/StringBuffer;)Ljava/lang/StringBuffer;"),
            Selector.make("appendCodePoint(I)Ljava/lang/StringBuffer;"),
            Selector.make("capacity()I"),
            Selector.make("charAt(I)C"),
            Selector.make("codePointAt(I)I"),
            Selector.make("codePointBefore(I)I"),
            Selector.make("codePointCount(II)I"),
            Selector.make("delete(II)Ljava/lang/StringBuffer;"),
            Selector.make("deleteCharAt(I)Ljava/lang/StringBuffer;"),
            Selector.make("ensureCapacity(I)V"),
            Selector.make("getChars(II[CI)V"),
            Selector.make("indexOf(Ljava/lang/String;)I"),
            Selector.make("indexOf(Ljava/lang/String;I)I"),
            Selector.make("insert(IB)Ljava/lang/StringBuffer;"),
            Selector.make("insert(IC)Ljava/lang/StringBuffer;"),
            Selector.make("insert(I[C)Ljava/lang/StringBuffer;"),
            Selector.make("insert(I[CII)Ljava/lang/StringBuffer;"),
            Selector.make("insert(ILjava/lang/CharSequence;)Ljava/lang/StringBuffer;"),
            Selector.make("insert(ILjava/lang/CharSequence;II)Ljava/lang/StringBuffer;"),
            Selector.make("insert(ID)Ljava/lang/StringBuffer;"),
            Selector.make("insert(IF)Ljava/lang/StringBuffer;"),
            Selector.make("insert(II)Ljava/lang/StringBuffer;"),
            Selector.make("insert(IJ)Ljava/lang/StringBuffer;"),
            Selector.make("insert(ILjava/lang/Object;)Ljava/lang/StringBuffer;"),
            Selector.make("insert(ILjava/lang/String;)Ljava/lang/StringBuffer;"),
            Selector.make("lastIndexOf(Ljava/lang/String;)I"),
            Selector.make("lastIndexOf(Ljava/lang/String;I)I"),
            Selector.make("length()I"),
            Selector.make("offsetByCodePoints(II)I"),
            Selector.make("replace(IILjava/lang/String;)Ljava/lang/StringBuffer;"),
            Selector.make("reverse()Ljava/lang/StringBuffer;"),
            Selector.make("setCharAt(IC)V"),
            Selector.make("setLength(I)V"),
            Selector.make("subSequence(II)Ljava/lang/CharSequence;"),
            Selector.make("subString(I)Ljava/lang/String;"),
            Selector.make("subString(II)Ljava/lang/String;"),
            Selector.make("toString()Ljava/lang/String;"),
            Selector.make("trimToSize()V"),
    };

    private IClassHierarchy cha;

    private static JavaStringBufferModelClass klass;
    private final IClass oriClass;

    public static JavaStringBufferModelClass getInstance(IClassHierarchy cha) {
        if(klass == null){
            klass = new JavaStringBufferModelClass((ClassHierarchy)cha);
        }
        return klass;
    }

    private JavaStringBufferModelClass(ClassHierarchy cha) {
        super(JAVA_STRINGBUFFER_MODEL_CLASS, cha);
        this.cha = cha;
        this.oriClass = cha.lookupClass(JAVA_STRINGBUFFER_MODEL_CLASS);
        initMethodsForHandler();

        this.addMethod(this.clinit());
//        cha.remove(JAVA_STRINGBUFFER_MODEL_CLASS);
        cha.addClass(this);
    }

    private void initMethodsForHandler(){
        for(Selector s : mSelectors){
            if(s.getDescriptor().getReturnType().equals(JAVA_STRINGBUFFER_MODEL_CLASS.getName()))
                addMethod(unconditionalRetNewString(s));
        }
    }

    /**
     *  sendToTarget()V
     */
    private SummarizedMethod unconditionalRetNewString(Selector s) {
        final MethodReference newStrRef = MethodReference.findOrCreate(this.getReference(), s);
        final VolatileMethodSummary newStr = new VolatileMethodSummary(new MethodSummary(newStrRef));
        newStr.setStatic(false);

        final TypeSafeInstructionFactory instructionFactory = new TypeSafeInstructionFactory(cha);

        int ssaNo = s.getDescriptor().getNumberOfParameters() + 2;

        // 2 = 1.target;
        final int newStrPC = newStr.getNextProgramCounter();
        final SSAValue strV = new SSAValue(ssaNo++, JAVA_STRINGBUFFER_MODEL_CLASS, newStrRef);
        final NewSiteReference strNSR = NewSiteReference.make(newStrPC, JAVA_STRINGBUFFER_MODEL_CLASS);
        final SSAInstruction newStrInst = instructionFactory.NewInstruction(newStrPC, strV, strNSR);
        newStr.addStatement(newStrInst);

        // sendMessage(2, 1);
        final int initStrPC = newStr.getNextProgramCounter();
        final CallSiteReference initStrCSR = CallSiteReference.make(initStrPC, MethodReference.findOrCreate(JAVA_STRINGBUFFER_MODEL_CLASS, Selector.make("<init>()V")), IInvokeInstruction.Dispatch.SPECIAL);
        final List<SSAValue> paramsInitStr = new ArrayList<SSAValue>();
        paramsInitStr.add(strV);
        final SSAValue initStrExcV = new SSAValue(ssaNo++, TypeReference.JavaLangException, newStrRef);
        final SSAInstruction initStrInst = instructionFactory.InvokeInstruction(initStrPC, paramsInitStr, initStrExcV, initStrCSR);
        newStr.addStatement(initStrInst);

        final int retPC = newStr.getNextProgramCounter();
        final SSAInstruction retInst = instructionFactory.ReturnInstruction(retPC, strV);
        newStr.addStatement(retInst);

        return new SummarizedMethodWithNames(newStrRef, newStr, this);
    }

    private SummarizedMethod clinit() {
        final MethodReference clinitRef = MethodReference.findOrCreate(this.getReference(), MethodReference.clinitSelector);
        final VolatileMethodSummary clinit = new VolatileMethodSummary(new MethodSummary(clinitRef));
        clinit.setStatic(true);

        return new SummarizedMethodWithNames(clinitRef, clinit, this);
    }
}
