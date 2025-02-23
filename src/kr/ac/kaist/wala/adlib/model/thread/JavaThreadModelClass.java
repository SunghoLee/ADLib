package kr.ac.kaist.wala.adlib.model.thread;

import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.classLoader.FieldImpl;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.ipa.summaries.MethodSummary;
import com.ibm.wala.ipa.summaries.SummarizedMethod;
import com.ibm.wala.ipa.summaries.SummarizedMethodWithNames;
import com.ibm.wala.ipa.summaries.VolatileMethodSummary;
import com.ibm.wala.shrikeBT.IInvokeInstruction;
import com.ibm.wala.shrikeCT.ClassConstants;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.types.*;
import com.ibm.wala.util.ssa.SSAValue;
import com.ibm.wala.util.ssa.TypeSafeInstructionFactory;
import com.ibm.wala.util.strings.Atom;
import kr.ac.kaist.wala.adlib.model.ModelClass;

import java.util.ArrayList;
import java.util.List;

/**
 * A modeling class for Java built-in java/lang/Thread.
 * Created by leesh on 14/01/2017.
 */
public class JavaThreadModelClass extends ModelClass {

    public static final TypeName RUNNABLE_TYPE_NAME = TypeName.string2TypeName("Ljava/lang/Runnable");

    public static final TypeReference JAVA_THREAD_MODEL_CLASS = TypeReference.findOrCreate(
            ClassLoaderReference.Primordial, TypeName.string2TypeName("Ljava/lang/Thread"));
    public static final Selector INIT_SELECTOR = Selector.make("<init>(Ljava/lang/Runnable;)V");
    public static final Selector RUN_SELECTOR = Selector.make("run()V");
    public static final Selector START_SELECTOR = Selector.make("start()V");
    public static final FieldReference TARGET_FIELD = FieldReference.findOrCreate(ClassLoaderReference.Primordial, "Ljava/lang/Thread", "target", "Ljava/lang/Runnable");

    private IClassHierarchy cha;

    private static JavaThreadModelClass klass;

    public static JavaThreadModelClass getInstance(IClassHierarchy cha) {
        if(klass == null){
            klass = new JavaThreadModelClass((ClassHierarchy)cha);
        }
        return klass;
    }

    private JavaThreadModelClass(ClassHierarchy cha) {
        super(JAVA_THREAD_MODEL_CLASS, cha);
        this.cha = cha;

        initMethodsForThread();
        initField();
        this.addMethod(this.clinit());

        //TODO: Solve the subclass problem: all subclasses must point to this new Thread class as their super class.
//        cha.remove(JAVA_THREAD_MODEL_CLASS);
        cha.addClass(this);
    }

    private void initMethodsForThread(){
        this.addMethod(this.runOfRunnable(START_SELECTOR));
        this.addMethod(this.init(INIT_SELECTOR));
    }

    private void initField(){
        FieldImpl targetF = new FieldImpl(this,
                FieldReference.findOrCreate(JAVA_THREAD_MODEL_CLASS, Atom.findOrCreateAsciiAtom("target"), TypeReference.find(ClassLoaderReference.Primordial, RUNNABLE_TYPE_NAME)),
                ClassConstants.ACC_PROTECTED,
                null, null);
        addField(targetF);
    }

    /**
     * <init>(Ljava/lang/Runnable;)V
     * @param s
     * @return
     */
    private SummarizedMethod init(Selector s) {
        final MethodReference initRef = MethodReference.findOrCreate(this.getReference(), s);
        final VolatileMethodSummary init = new VolatileMethodSummary(new MethodSummary(initRef));
        init.setStatic(false);
        final TypeSafeInstructionFactory instructionFactory = new TypeSafeInstructionFactory(cha);

        int ssaNo = 3;
        final TypeReference runnableTR = TypeReference.findOrCreate(ClassLoaderReference.Primordial, "Ljava/lang/Runnable");
        final SSAValue threadArgV = new SSAValue(1, JAVA_THREAD_MODEL_CLASS, initRef);
        final SSAValue runnableArgV = new SSAValue(2, runnableTR, initRef);

        // 1.target = 2;
        final int putTargetpc = init.getNextProgramCounter();
        init.addStatement(instructionFactory.PutInstruction(putTargetpc, threadArgV, runnableArgV, FieldReference.findOrCreate(JAVA_THREAD_MODEL_CLASS, Atom.findOrCreateAsciiAtom("target"), runnableTR)));

        // return;
        final int retpc = init.getNextProgramCounter();
        init.addStatement(instructionFactory.ReturnInstruction(retpc));

        return new SummarizedMethodWithNames(initRef, init, this);
    }


    /**
     *  Generate run of Thread for AndroidThreadModelClass.
     *
     *  run call Runnable's run method
     */
    private SummarizedMethod runOfRunnable(Selector s) {
        final MethodReference runRef = MethodReference.findOrCreate(this.getReference(), s);
        final VolatileMethodSummary run = new VolatileMethodSummary(new MethodSummary(runRef));
        run.setStatic(false);
        final TypeSafeInstructionFactory instructionFactory = new TypeSafeInstructionFactory(cha);

        int ssaNo = 1;

        TypeReference timerTaskTR = TypeReference.findOrCreate(ClassLoaderReference.Application, RUNNABLE_TYPE_NAME);

        final SSAValue timerTaskV = new SSAValue(ssaNo++, timerTaskTR, runRef);
        final int pc = run.getNextProgramCounter();
        final MethodReference timerTaskRunMR = MethodReference.findOrCreate(timerTaskTR, RUN_SELECTOR);
        final List<SSAValue> params = new ArrayList<SSAValue>();
        params.add(timerTaskV);
        final SSAValue exception = new SSAValue(ssaNo++, TypeReference.JavaLangException, runRef);
        final CallSiteReference site = CallSiteReference.make(pc, timerTaskRunMR, IInvokeInstruction.Dispatch.VIRTUAL);

        final SSAInstruction runCall = instructionFactory.InvokeInstruction(pc, params, exception, site);
        run.addStatement(runCall);

        final SSAValue targetV = new SSAValue(ssaNo++, timerTaskTR, runRef);
        final int pc_get_target = run.getNextProgramCounter();
        final SSAInstruction getTarget = instructionFactory.GetInstruction(pc_get_target, targetV, timerTaskV, TARGET_FIELD);
        run.addStatement(getTarget);

        final int pc_call_target = run.getNextProgramCounter();
        final List<SSAValue> paramsCallTarget = new ArrayList<SSAValue>();
        paramsCallTarget.add(targetV);
        final SSAValue exceptionCallTarget = new SSAValue(ssaNo++, TypeReference.JavaLangException, runRef);
        final CallSiteReference siteCallTarget = CallSiteReference.make(pc, timerTaskRunMR, IInvokeInstruction.Dispatch.VIRTUAL);

        final SSAInstruction runCallTarget = instructionFactory.InvokeInstruction(pc_call_target, paramsCallTarget, exceptionCallTarget, siteCallTarget);
        run.addStatement(runCallTarget);

        final int pc_ret = run.getNextProgramCounter();
        final SSAInstruction retInst = instructionFactory.ReturnInstruction(pc_ret);
        run.addStatement(retInst);

        return new SummarizedMethodWithNames(runRef, run, this);
    }

    private SummarizedMethod clinit() {
        final MethodReference clinitRef = MethodReference.findOrCreate(this.getReference(), MethodReference.clinitSelector);
        final VolatileMethodSummary clinit = new VolatileMethodSummary(new MethodSummary(clinitRef));
        clinit.setStatic(true);

        return new SummarizedMethodWithNames(clinitRef, clinit, this);
    }
}
