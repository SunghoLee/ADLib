package kr.ac.kaist.wala.adlib.model.string;

import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.ipa.summaries.MethodSummary;
import com.ibm.wala.ipa.summaries.SummarizedMethod;
import com.ibm.wala.ipa.summaries.SummarizedMethodWithNames;
import com.ibm.wala.ipa.summaries.VolatileMethodSummary;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.types.*;
import com.ibm.wala.util.ssa.SSAValue;
import com.ibm.wala.util.ssa.TypeSafeInstructionFactory;
import kr.ac.kaist.wala.adlib.model.ModelClass;

/**
 * A modeling class for Android built-in android/os/Handler.
 * Created by leesh on 14/01/2017.
 */
public class JavaAbstractStringBuilderModelClass extends ModelClass {

    public static final TypeReference JAVA_ABSTRACTSTRINGBUILDER_MODEL_CLASS = TypeReference.findOrCreate(
            ClassLoaderReference.Primordial, TypeName.string2TypeName("Ljava/lang/AbstractStringBuilder"));

    public static final Selector[] mSelectors = {
            Selector.make("<init>()V"),
            Selector.make("<init>(Ljava/lang/CharSequence;)V"),
            Selector.make("<init>(I)V"),
            Selector.make("<init>(Ljava/lang/String;)V"),
//            Selector.make("append(Z)Ljava/lang/StringBuilder;"),
//            Selector.make("append(C)Ljava/lang/StringBuilder;"),
//            Selector.make("append([C)Ljava/lang/StringBuilder;"),
//            Selector.make("append([CII)Ljava/lang/StringBuilder;"),
//            Selector.make("append(Ljava/lang/CharSequence;)Ljava/lang/StringBuilder;"),
//            Selector.make("append(Ljava/lang/CharSequence;II)Ljava/lang/StringBuilder;"),
//            Selector.make("append(D)Ljava/lang/StringBuilder;"),
//            Selector.make("append(F)Ljava/lang/StringBuilder;"),
//            Selector.make("append(I)Ljava/lang/StringBuilder;"),
//            Selector.make("append(J)Ljava/lang/StringBuilder;"),
//            Selector.make("append(Ljava/lang/Object;)Ljava/lang/StringBuilder;"),
//            Selector.make("append(Ljava/lang/String;)Ljava/lang/StringBuilder;"),
//            Selector.make("append(Ljava/lang/StringBuffer;)Ljava/lang/StringBuilder;"),
//            Selector.make("appendCodePoint(I)Ljava/lang/StringBuilder;"),
            Selector.make("capacity()I"),
            Selector.make("charAt(I)C"),
            Selector.make("codePointAt(I)I"),
            Selector.make("codePointBefore(I)I"),
            Selector.make("codePointCount(II)I"),
//            Selector.make("delete(II)Ljava/lang/StringBuilder;"),
//            Selector.make("deleteCharAt(I)Ljava/lang/StringBuilder;"),
            Selector.make("ensureCapacity(I)V"),
            Selector.make("getChars(II[CI)V"),
            Selector.make("indexOf(Ljava/lang/String;)I"),
            Selector.make("indexOf(Ljava/lang/String;I)I"),
//            Selector.make("insert(IZ)Ljava/lang/StringBuilder;"),
//            Selector.make("insert(IC)Ljava/lang/StringBuilder;"),
//            Selector.make("insert(I[C)Ljava/lang/StringBuilder;"),
//            Selector.make("insert(I[CII)Ljava/lang/StringBuilder;"),
//            Selector.make("insert(ILjava/lang/CharSequence;)Ljava/lang/StringBuilder;"),
//            Selector.make("insert(ILjava/lang/CharSequence;II)Ljava/lang/StringBuilder;"),
//            Selector.make("insert(ID)Ljava/lang/StringBuilder;"),
//            Selector.make("insert(IF)Ljava/lang/StringBuilder;"),
//            Selector.make("insert(II)Ljava/lang/StringBuilder;"),
//            Selector.make("insert(IJ)Ljava/lang/StringBuilder;"),
//            Selector.make("insert(ILjava/lang/Object;)Ljava/lang/StringBuilder;"),
//            Selector.make("insert(ILjava/lang/String;)Ljava/lang/StringBuilder;"),
            Selector.make("lastIndexOf(Ljava/lang/String;)I"),
            Selector.make("lastIndexOf(Ljava/lang/String;I)I"),
            Selector.make("length()I"),
            Selector.make("offsetByCodePoints(II)I"),
//            Selector.make("replace(IILjava/lang/String;)Ljava/lang/StringBuilder;"),
            Selector.make("reverse()Ljava/lang/StringBuilder;"),
            Selector.make("setCharAt(IC)V"),
            Selector.make("setLength(I)V"),
            Selector.make("subSequence(II)Ljava/lang/CharSequence;"),
            Selector.make("subString(I)Ljava/lang/String;"),
            Selector.make("subString(II)Ljava/lang/String;"),
            Selector.make("toString()Ljava/lang/String;"),
            Selector.make("trimToSize()V"),
    };

    private IClassHierarchy cha;

    private static JavaAbstractStringBuilderModelClass klass;
    private final IClass oriClass;

    public static JavaAbstractStringBuilderModelClass getInstance(IClassHierarchy cha) {
        if(klass == null){
            klass = new JavaAbstractStringBuilderModelClass((ClassHierarchy)cha);
        }
        return klass;
    }

    private JavaAbstractStringBuilderModelClass(ClassHierarchy cha) {
        super(JAVA_ABSTRACTSTRINGBUILDER_MODEL_CLASS, cha);
        this.cha = cha;
        this.oriClass = cha.lookupClass(JAVA_ABSTRACTSTRINGBUILDER_MODEL_CLASS);
        initMethodsForHandler();

        this.addMethod(this.clinit());
//        cha.remove(JAVA_ABSTRACTSTRINGBUILDER_MODEL_CLASS);
        cha.addClass(this);
    }

    private void initMethodsForHandler(){
        for(Selector s : mSelectors){
            if(s.getDescriptor().getReturnType().equals(JAVA_ABSTRACTSTRINGBUILDER_MODEL_CLASS.getName())) {
                if(s.toString().startsWith("insert") || s.toString().startsWith("append"))
                    addMethod(retThisString(s));
            }
        }
    }

    private SummarizedMethod retThisString(Selector s) {
        final MethodReference newStrRef = MethodReference.findOrCreate(this.getReference(), s);
        final VolatileMethodSummary newStr = new VolatileMethodSummary(new MethodSummary(newStrRef));
        newStr.setStatic(false);

        final TypeSafeInstructionFactory instructionFactory = new TypeSafeInstructionFactory(cha);

        final SSAValue strV = new SSAValue(1, JAVA_ABSTRACTSTRINGBUILDER_MODEL_CLASS, newStrRef);

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
