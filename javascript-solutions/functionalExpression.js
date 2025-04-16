const cnst = (value) => () => value;

const pi = cnst(Math.PI);
const tau = cnst(2 * Math.PI);
const phi = cnst((1 + Math.sqrt(5)) / 2);

const operation = (f, arity) => (...ops) => (...args) => f(...ops.slice(0, arity).map(op => op(...args)));

const variables = ['x', 'y', 'z', 't'];
// :NOTE: `?? 0` не нужно
// :NOTE: и args[0] не может быть объектом
const variable = name => (...args) => (args.length === 1 && typeof args[0] === 'object') ? args[0][name] ?? 0 : args[variables.indexOf(name)] ?? 0;

const add = operation((a, b) => a + b, 2); // :NOTE: arity ни на что не влияет здесь
const subtract = operation((a, b) => a - b, 2);
const multiply = operation((a, b) => a * b, 2);
const divide = operation((a, b) => a / b, 2);
const negate = operation(a => -a, 1);

const less3 = operation((a, b, c) => (a < b && b < c) ? 1 : 0, 3);
const greater4 = operation((a, b, c, d) => (a > b && b > c && c > d) ? 1 : 0, 4);

const OPERATIONS = {
    '+': {op: add, element: 2},
    '-': {op: subtract, element: 2},
    '*': {op: multiply, element: 2},
    '/': {op: divide, element: 2},
    'negate': {op: negate, element: 1},
    'less3': {op: less3, element: 3},
    'greater4': {op: greater4, element: 4}
};

const CONSTANTS_MAP = {
    'pi': pi,
    'tau': tau,
    'phi': phi
};

function parse(expr) {
    const tokens = expr.trim().split(/\s+/);

    const stack = [];

    for (const token of tokens) {
        if (token in OPERATIONS) {
            const {op, element} = OPERATIONS[token];
            const args = stack.splice(-element);
            stack.push(op(...args));
        } else if (token in CONSTANTS_MAP) {
            stack.push(CONSTANTS_MAP[token]);
        } else if (variables.includes(token)) {
            stack.push(variable(token));
        } else {
            stack.push(cnst(parseFloat(token)));
        }
    }
    
    if (stack.length !== 1) throw new Error('Invalid expression');
    return stack[0];
}