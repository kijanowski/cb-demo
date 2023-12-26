Diagrams have been created with [Mermaid](https://mermaid.js.org/) and can be downloaded through the [live UI](https://mermaid.live/).

- `state_operations.svg`
```
%%{init: {'themeVariables': { 'edgeLabelBackground': 'white'}}}%%

graph LR;
    U1(Client\nfa:fa-satellite-dish) -- fa:fa-hand-holding --> C1
    C1(CLOSED fa:fa-lock\n<i class="fa-solid fa-magnifying-glass-chart"></i>)-- fa:fa-hand-holding -->T1(3rd party\nfa:fa-thumbs-up)
    T1-- fa:fa-apple-whole -->C1
    C1-- fa:fa-apple-whole -->U1


    U2(Client\nfa:fa-satellite-dish) -- fa:fa-hand-holding -->C2
    C2(OPENED fa:fa-unlock\n<i class="fa-solid fa-magnifying-glass-chart"></i>)~~~T2(3rd party\nfa:fa-dizzy)
    T2~~~C2
    C2-- fa:fa-carrot -->U2

    U3(Client\nfa:fa-satellite-dish) -- fa:fa-hand-holding -->C3
    U3(Client\nfa:fa-satellite-dish) -- fa:fa-hand-holding -->C3
    C3(HALF_OPENED fa:fa-clipboard-question\n<i class="fa-solid fa-magnifying-glass-chart"></i>)-- fa:fa-hand-holding -->T3(3rd party\nfa:fa-thumbs-up)
    T3-- fa:fa-apple-whole -->C3
    C3-- fa:fa-carrot -->U3
    C3-- fa:fa-apple-whole -->U3 
```

- `state_transitions.svg`
```
graph TD;
    C(CLOSED\nfa:fa-lock)-->O(OPEN);
    O-->H(HALF_OPEN);
    H-->O(OPEN\nfa:fa-unlock);
    H(HALF_OPEN\nfa:fa-microscope)-->C;
```

- `state_transitions_ext.svg`
```
%%{init: {'themeVariables': { 'edgeLabelBackground': 'white'}}}%%
graph TD;
    C(CLOSED)-->|20% fa:fa-skull| O(OPEN);
    O-->H(HALF_OPEN);
    H-->|20% fa:fa-skull|O(OPEN fa:fa-clock 20s);
    H(HALF_OPEN\nfa:fa-satellite-dish fa:fa-satellite-dish)-->C;
```

- `window_long.svg`
```
graph TD;
    W1(COUNT_BASED Window\nfa:fa-circle-check fa:fa-circle-check fa:fa-circle-check fa:fa-circle-check fa:fa-circle-check fa:fa-circle-check fa:fa-circle-check fa:fa-skull fa:fa-circle-check fa:fa-skull)
    O((OPEN))
    W1 --> O
```
- `window_short.svg`
```
graph TD;
    W2(COUNT_BASED Window\nfa:fa-circle-check fa:fa-circle-check fa:fa-circle-check fa:fa-skull fa:fa-skull fa:fa-eye-slash fa:fa-eye-slash fa:fa-eye-slash fa:fa-eye-slash fa:fa-eye-slash)
    O((OPEN))
    W2 --> O
```