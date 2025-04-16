class Expression {
    evaluate() {}
    toString() {}
    diff() {}
    simplify() { return this; }
    prefix() { return this.toString() }
    postfix() { return this.toString() }
}

const VARIABLES = ['x', 'y', 'z'];

class Const extends Expression {
    constructor(value) {
        super();
        this.value = value;
    }
    evaluate() {
        return this.value;
    }
    toString() {
        return this.value.toString();
    }
    diff() {
        return new Const(0);
    }
    simplify() { return this; }
    postfix() {
        return this.value.toString();
    }
}

class Variable extends Expression {
    constructor(name) {
        super();
        this.name = name;
        this.index = VARIABLES.indexOf(name);
    }
    evaluate(...args) {
        return args[this.index];
    }
    toString() {
        return this.name;
    }
    diff(v) {
        return new Const(this.name === v ? 1 : 0);
    }
    simplify() { return this; }
    prefix() {
        return this.name;
    }
}

class BinaryOperation extends Expression {
    constructor(a, b, operation, operationFunc) {
        super();
        this.a = a;
        this.b = b;
        this.operation = operation;
        this.operationFunc = operationFunc; 
    }
    evaluate(...args) {
        return this.operationFunc(
            this.a.evaluate(...args),
            this.b.evaluate(...args)
        );
    }
    toString() { return `${this.a} ${this.b} ${this.operation}`; }
    prefix() {
        return `(${this.operation} ${this.a.prefix()} ${this.b.prefix()})`; 
    }
    postfix() { return `(${this.a.postfix()} ${this.b.postfix()} ${this.operation})`; }
}

class UnaryOperation extends Expression {
    constructor(a, operator, operationFunc) {
        super();
        this.a = a;
        this.operator = operator;
        this.operationFunc = operationFunc;
    }
    toString() { return `${this.a} ${this.operator}`; }
    prefix() { return `(${this.operator} ${this.a.prefix()})`; }
    postfix() { return `(${this.a.postfix()} ${this.operator})`; }
    evaluate(...args) {
        return this.operationFunc(this.a.evaluate(...args));
    }
}

class MultiOperation extends Expression {
    constructor(...operands) {
        super();
        this.operands = operands;
    }
    toString() {
        return `${this.operands.join(' ')} ${this.operator}`;
    }
    prefix() {
        return `(${this.operator} ${this.operands.map(op => op.prefix()).join(' ')})`;
    }
    postfix() {
        return `(${this.operands.map(op => op.postfix()).join(' ')} ${this.operator})`;
    }
    simplify() {
        const simplified = this.operands.map(op => op.simplify());
        return new this.constructor(...simplified);
    }
}

class Add extends BinaryOperation {
    constructor(a, b) {
        super(a, b, "+", (x, y) => x + y);
        this.a = a;
        this.b = b;
    }
    diff(v) {
        return new Add(this.a.diff(v), this.b.diff(v));
    }
    simplify() {
        const a = this.a.simplify();
        const b = this.b.simplify();
        if (a instanceof Const && b instanceof Const) {
            return new Const(a.value + b.value);
        }
        if (a instanceof Const && a.value === 0) return b;
        if (b instanceof Const && b.value === 0) return a;
        return new Add(a, b);
    }
}

class Subtract extends BinaryOperation {
    constructor(a, b) {
        super(a, b, "-", (x, y) => x - y);
        this.a = a;
        this.b = b;
    }
    diff(v) {
        return new Subtract(this.a.diff(v), this.b.diff(v));
    }
    simplify() {
        const a = this.a.simplify();
        const b = this.b.simplify();
        if (a instanceof Const && b instanceof Const) {
            return new Const(a.value - b.value);
        }
        if (b instanceof Const && b.value === 0) return a;
        return new Subtract(a, b);
    }
}

class Multiply extends BinaryOperation {
    constructor(a, b) {
        super(a, b, "*", (x, y) => x * y);
        this.a = a;
        this.b = b;
    }
    diff(v) {
        return new Add(
            new Multiply(this.a.diff(v), this.b),
            new Multiply(this.a, this.b.diff(v))
        );
    }
    simplify() {
        const a = this.a.simplify();
        const b = this.b.simplify();

        if (a instanceof Negate && b instanceof Negate) {
            return new Multiply(a.a, b.a).simplify();
        }

        if (a instanceof Negate) {
            return new Negate(new Multiply(a.a, b).simplify()).simplify();
        }
        if (b instanceof Negate) {
            return new Negate(new Multiply(a, b.a).simplify()).simplify();
        }

        if (a instanceof Const && b instanceof Const) {
            return new Const(a.value * b.value);
        }
        if (a instanceof Const && a.value === 0) return new Const(0);
        if (a instanceof Const && a.value === 1) return b;
        if (b instanceof Const && b.value === 0) return new Const(0);
        if (b instanceof Const && b.value === 1) return a;

        return new Multiply(a, b);
    }
}

class Divide extends BinaryOperation {
    constructor(a, b) {
        super(a, b, "/", (x, y) => x / y);
        this.a = a;
        this.b = b;
    }
    diff(v) {
        const simplified = new Divide(
            new Subtract(
                new Multiply(this.a.diff(v), this.b),
                new Multiply(this.a, this.b.diff(v))
            ),
            new Multiply(this.b, this.b)
        ).simplify();
        return simplified;
    }
    simplify() {
        const a = this.a.simplify();
        const b = this.b.simplify();

        if (a instanceof Const && b instanceof Const) {
            if (b.value === 0) return a.value === 0 ? new Const(0) : new Divide(a, b);
            return new Const(a.value / b.value);
        }

        if (a instanceof Const && a.value === 0) return new Const(0);
        if (b instanceof Const && b.value === 1) return a;

        if (b instanceof Multiply) {
            const bParts = [b.a, b.b];
            const commonIndex = bParts.findIndex(part => part.toString() === a.toString());
            if (commonIndex !== -1) {
                const remaining = bParts.filter((_, i) => i !== commonIndex);
                if (remaining.length === 0) return new Const(1);
                return new Divide(new Const(1), remaining.reduce((acc, part) => new Multiply(acc, part)));
            }
        }

        return new Divide(a, b);
    }
}
class Negate extends UnaryOperation {
    constructor(a) {
        super(a, "negate", x => -x);
        this.a = a;
    }
    diff(v) {
        return new Negate(this.a.diff(v));
    }
    simplify() {
        const a = this.a.simplify();
        if (a instanceof Const) return new Const(-a.value);
        if (a instanceof Negate) return a.a.simplify();
        return new Negate(a);
    }
}

class NormalBase extends MultiOperation {
    constructor(n, ...operands) {
        super(...operands);
        this.n = n;
    }
    get operator() { return `normal${this.n}`; }
    evaluate(...args) {
        const sum = this.operands.reduce((acc, op) => acc + Math.pow(op.evaluate(...args), 2), 0);
        return Math.exp(-sum / 2) / Math.pow(2 * Math.PI, this.n / 2);
    }
    diff(v) {
        const terms = this.operands.map(operand => {
            const dOperand = operand.diff(v);
            if (dOperand instanceof Const && dOperand.value === 0) {
                return null;
            }
            return new Multiply(
                new Negate(operand),
                new Multiply(
                    dOperand,
                    new NormalBase(this.n, ...this.operands)
                )
            );
        }).filter(term => term !== null);

        if (terms.length === 0) {
            return new Const(0);
        }

        return terms.reduce((acc, term) => new Add(acc, term));
    }
    simplify() {
        const simplifiedOperands = this.operands.map(op => op.simplify());
        const optimizedOperands = simplifiedOperands.map(op => {
            if (op instanceof Negate) return op.a;
            return op;
        });
        if (optimizedOperands.every(op => op instanceof Const)) {
            const sum = optimizedOperands.reduce((acc, op) => acc + Math.pow(op.value, 2), 0);
            return new Const(Math.exp(-sum / 2) / Math.pow(2 * Math.PI, this.n / 2));
        }
        return new NormalBase(this.n, ...optimizedOperands);
    }
}

class SumCb extends MultiOperation {
    get operator() { return 'sumCb'; }
    evaluate(...args) {
        return this.operands.reduce((sum, op) => sum + Math.pow(op.evaluate(...args), 3), 0);
    }
    diff(v) {
        return this.operands.map(op => 
            new Multiply(new Const(3), new Multiply(new Multiply(op, op), op.diff(v)))
        ).reduce((acc, curr) => new Add(acc, curr), new Const(0));
    }
    simplify() {
        const simplified = this.operands.map(op => op.simplify());
        if (simplified.every(op => op instanceof Const)) {
            const sum = simplified.reduce((s, op) => s + Math.pow(op.value, 3), 0);
            return new Const(sum);
        }
        return new SumCb(...simplified);
    }
}

class Power extends BinaryOperation {
    constructor(base, exponent) {
        super(base, exponent, "pow", Math.pow);
        this.base = base;
        this.exponent = exponent;
    }
    diff(v) {
        return new Multiply(
            new Multiply(
                this.exponent,
                new Power(this.base, new Subtract(this.exponent, new Const(1)))
            ),
            this.base.diff(v)
        );
    }
    simplify() {
        const base = this.base.simplify();
        const exponent = this.exponent.simplify();
        if (base instanceof Const && exponent instanceof Const) {
            return new Const(Math.pow(base.value, exponent.value));
        }
        return new Power(base, exponent);
    }
}

class Rmc extends MultiOperation {
    get operator() { return 'rmc'; }
    evaluate(...args) {
        const sumCb = this.operands.reduce((sum, op) => sum + Math.pow(op.evaluate(...args), 3), 0);
        return Math.cbrt(sumCb / this.operands.length);
    }
    diff(v) {
        const sumCb = new SumCb(...this.operands);
        const mean = new Divide(sumCb, new Const(this.operands.length));
        const dMean = new Divide(sumCb.diff(v), new Const(this.operands.length));
    
        const simplifiedMean = mean.simplify();
        if (simplifiedMean instanceof Const && simplifiedMean.value === 0) {
            return new Const(0);
        }
    
        const powerTerm = new Power(mean, new Const(-2/3));
        return new Multiply(
            new Divide(new Const(1), new Const(3)),
            new Multiply(powerTerm, dMean)
        );
    }
    simplify() {
        const simplified = this.operands.map(op => op.simplify());
        if (simplified.every(op => op instanceof Const)) {
            const sumCb = simplified.reduce((sum, op) => sum + Math.pow(op.value, 3), 0);
            return new Const(Math.cbrt(sumCb / simplified.length));
        }
        return new Rmc(...simplified);
    }
}

const CONSTANTS = {
    'pi': new Const(Math.PI),
    'tau': new Const(2 * Math.PI),
    'phi': new Const((1 + Math.sqrt(5)) / 2)
};

const OPERATIONS = {
    '+': { class: Add, arity: 2 },
    '-': { class: Subtract, arity: 2 },
    '*': { class: Multiply, arity: 2 },
    '/': { class: Divide, arity: 2 },
    'negate': { class: Negate, arity: 1 },
    'sumCb': { class: SumCb, arity: 'any' },
    'rmc': { class: Rmc, arity: 'any' },
    'pow': { class: Power, arity: 2 }
};

for (let n = 1; n <= 5; n++) {
    globalThis[`Normal${n}`] = class extends NormalBase {
        constructor(...operands) {
            super(n, ...operands);
        }
    };
    OPERATIONS[`normal${n}`] = { class: globalThis[`Normal${n}`], arity: n };
}


function parse(expr) {
    const tokens = expr.trim().split(/\s+/);
    const stack = [];
    for (const token of tokens) {
        if (token in OPERATIONS) {
            const { class: OpClass, arity } = OPERATIONS[token];
            const args = stack.splice(-arity);
            stack.push(new OpClass(...args));
        } else if (token in CONSTANTS) {
            stack.push(CONSTANTS[token]);
        } else if (VARIABLES.includes(token)) {
            stack.push(new Variable(token));
        } else {
            const num = parseFloat(token);
            if (!isNaN(num)) {
                stack.push(new Const(num));
            } else {
                throw new Error(`Unknown token: ${token}`);
            }
        }
    }
    if (stack.length !== 1) throw new Error('Invalid expression');
    return stack[0];
}

function tokenize(expr) {
    const tokens = [];
    let current = '';
    for (const c of expr) {
        if (c === '(' || c === ')') {
            if (current) tokens.push(current);
            tokens.push(c);
            current = '';
        } else if (/\s/.test(c)) {
            if (current) tokens.push(current);
            current = '';
        } else {
            current += c;
        }
    }
    if (current) tokens.push(current);
    return tokens;
}
//NOTE: copypaste
function parsePrefix(expr) {
    const tokens = tokenize(expr);
    let index = 0;
    const len = tokens.length;

    function parseExpr() {
        if (index >= len) throw new Error('Unexpected end of input');
        const token = tokens[index++];
        if (token === '(') {
            const op = tokens[index++];
            let args = [];
            while (tokens[index] !== ')') {
                if (index >= len) throw new Error('Missing )');
                args.push(parseExpr());
            }
            index++; 
            if (op in OPERATIONS) {
                const { class: Cls, arity } = OPERATIONS[op];
                if (arity !== 'any' && args.length !== arity) {
                    throw new Error(`Invalid number of arguments for ${op}`);
                }
                return new Cls(...args);
            } else {
                throw new Error(`Unknown operator: ${op}`);
            }
        } else if (token === ')') {
            throw new Error('Unexpected )');
        } else if (token in CONSTANTS) {
            return CONSTANTS[token];
        } else if (VARIABLES.includes(token)) {
            return new Variable(token);
        } else if (!isNaN(token)) {
            return new Const(parseFloat(token));
        } else {
            throw new Error(`Unknown token: ${token}`);
        }
    }

    const result = parseExpr();
    if (index !== len) throw new Error('Unexpected tokens');
    return result;
}
//NOTE: unclear error location
function parsePostfix(expr) {
    const tokens = tokenize(expr).reverse();
    
    let index = 0;
    const len = tokens.length;

    function parseExpr() {
        if (index >= len) throw new Error('Unexpected end of input');
        const token = tokens[index++];
        if (token === ')') {
            const op = tokens[index++];
            let args = [];
            while (tokens[index] !== '(') {
                if (index >= len) throw new Error('Missing )');
                args.push(parseExpr());
            }
            index++; 
            if (op in OPERATIONS) {
                const { class: Cls, arity } = OPERATIONS[op];
                if (arity !== 'any' && args.length !== arity) 
                    throw new Error(`Invalid number of arguments for ${op}`);
                if (op !== 'negate') {
                    args = args.reverse();
                }
                return new Cls(...args);
            } else {
                throw new Error(`Unknown operator: ${op}`);
            }
        } else if (token === '(') {
            throw new Error('Unexpected )');
        } else if (token in CONSTANTS) {
            return CONSTANTS[token];
        } else if (VARIABLES.includes(token)) {
            return new Variable(token);
        } else if (!isNaN(token)) {
            return new Const(parseFloat(token));
        } else {
            throw new Error(`Unknown token: ${token}`);
        }
    }

    const result = parseExpr();
    if (index !== len) throw new Error('Unexpected tokens');
    return result;
}