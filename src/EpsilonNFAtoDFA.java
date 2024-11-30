import java.util.*;

public class EpsilonNFAtoDFA {
    private Set<String> nfaStates;
    private Set<String> alphabet;
    private Map<String, Map<String, Set<String>>> nfaTransitions;
    private String nfaStartState;
    private Set<String> nfaAcceptStates;

    private Set<Set<String>> dfaStates;
    private Map<Set<String>, Map<String, Set<String>>> dfaTransitions;
    private Set<String> dfaAcceptStates;

    public EpsilonNFAtoDFA(Set<String> nfaStates, Set<String> alphabet,
                           Map<String, Map<String, Set<String>>> nfaTransitions,
                           String nfaStartState, Set<String> nfaAcceptStates) {
        this.nfaStates = nfaStates;
        this.alphabet = alphabet;
        this.nfaTransitions = nfaTransitions;
        this.nfaStartState = nfaStartState;
        this.nfaAcceptStates = nfaAcceptStates;
        this.dfaStates = new HashSet<Set<String>>();
        this.dfaTransitions = new HashMap<>();
        this.dfaAcceptStates = new HashSet<>();
    }

    public void convert() {
        // 1. Epsilon-kaplamayı hesapla
        Map<String, Set<String>> epsilonClosures = calculateEpsilonClosures();

        // 2. DFA başlangıç durumu
        Set<String> dfaStartState = epsilonClosures.get(nfaStartState);
        Queue<Set<String>> queue = new LinkedList<>();
        queue.add(dfaStartState);
        Set<Set<String>> visitedStates = new HashSet<>();
        visitedStates.add(dfaStartState);

        while (!queue.isEmpty()) {
            Set<String> currentDFAState = queue.poll();
            dfaStates.add(currentDFAState);

            Map<String, Set<String>> dfaTransitionsForCurrentState = new HashMap<>();

            for (String symbol : alphabet) {
                Set<String> newState = new HashSet<>();
                for (String nfaState : currentDFAState) {
                    Set<String> reachableStates = nfaTransitions.getOrDefault(nfaState, new HashMap<>()).getOrDefault(symbol, new HashSet<>());
                    for (String reachableState : reachableStates) {
                        newState.addAll(epsilonClosures.get(reachableState));
                    }
                }
                if (!newState.isEmpty()) {
                    dfaTransitionsForCurrentState.put(symbol, newState);
                    if (!visitedStates.contains(newState)) {
                        queue.add(newState);
                        visitedStates.add(newState);
                    }
                }
            }
            dfaTransitions.put(currentDFAState, dfaTransitionsForCurrentState);
        }

        // 3. DFA kabul durumlarını belirle
        for (Set<String> dfaState : dfaStates) {
            for (String nfaAcceptState : nfaAcceptStates) {
                if (dfaState.contains(nfaAcceptState)) {
                    dfaAcceptStates.add(String.join(",", dfaState));
                    break;
                }
            }
        }
    }

    private Map<String, Set<String>> calculateEpsilonClosures() {
        Map<String, Set<String>> epsilonClosures = new HashMap<>();
        for (String state : nfaStates) {
            Set<String> closure = new HashSet<>();
            calculateEpsilonClosure(state, closure);
            epsilonClosures.put(state, closure);
        }
        return epsilonClosures;
    }

    private void calculateEpsilonClosure(String state, Set<String> closure) {
        if (closure.contains(state)) return;
        closure.add(state);
        Set<String> epsilonTransitions = nfaTransitions.getOrDefault(state, new HashMap<>()).getOrDefault("ε", new HashSet<>());
        for (String nextState : epsilonTransitions) {
            calculateEpsilonClosure(nextState, closure);
        }
    }

    public void printNFA() {
        System.out.println("EpsilonNFA:");
        System.out.println("States: " + nfaStates);
        System.out.println("Alphabet: " + alphabet);
        System.out.println("Start State: " + nfaStartState);
        System.out.println("Accept States: " + nfaAcceptStates);
        System.out.println("Transitions: " + nfaTransitions);
    }

    public void printDFA() {
        System.out.println("\nDFA:");
        System.out.println("States: " + dfaStates);
        System.out.println("Alphabet: " + alphabet);
        System.out.println("Accept States: " + dfaAcceptStates);
        System.out.println("Transitions: ");
        for (Map.Entry<Set<String>, Map<String, Set<String>>> entry : dfaTransitions.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
    }

    public static void main(String[] args) {
        Set<String> nfaStates = new HashSet<>(Arrays.asList("q0", "q1", "q2", "q3"));
        Set<String> alphabet = new HashSet<>(Arrays.asList("0", "1"));
        Map<String, Map<String, Set<String>>> nfaTransitions = new HashMap<>();

        nfaTransitions.put("q0", new HashMap<>());
        nfaTransitions.get("q0").put("ε", new HashSet<>(Arrays.asList("q1", "q2")));
        nfaTransitions.put("q1", new HashMap<>());
        nfaTransitions.get("q1").put("0", new HashSet<>(Arrays.asList("q1")));
        nfaTransitions.put("q2", new HashMap<>());
        nfaTransitions.get("q2").put("1", new HashSet<>(Arrays.asList("q3")));
        nfaTransitions.put("q3", new HashMap<>());

        String nfaStartState = "q0";
        Set<String> nfaAcceptStates = new HashSet<>(Collections.singletonList("q3"));

        EpsilonNFAtoDFA converter = new EpsilonNFAtoDFA(nfaStates, alphabet, nfaTransitions, nfaStartState, nfaAcceptStates);

        converter.printNFA();
        converter.convert();
        converter.printDFA();
    }
}
