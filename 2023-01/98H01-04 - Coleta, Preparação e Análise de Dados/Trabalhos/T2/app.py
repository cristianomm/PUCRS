'''
Nome: Cristiano Moreira Martins
Matrícula: 11111871-7
'''


from flask import Flask, request, jsonify, make_response
import pandas_datareader as pdr
import pandas as pd

app = Flask(__name__)

def load_data():
    global symbols_data
    try:
        symbols_data = None
        symbols_data = pdr.nasdaq_trader.get_nasdaq_symbols()
    except Exception as e:
        print(e)

    if symbols_data is None:
        print('Não foi possível carregar os dados.')
        print('Buscando do arquivo local...')
        symbols_data = pd.read_csv('data/symbols.csv')
        symbols_data.reset_index(inplace=True)

    symbols_data = symbols_data.rename(
        columns = {
            'Symbol': 'symbol',
            'NASDAQ Symbol': 'NASDAQSymbol',
            'Nasdaq Traded': 'isTraded',
            'Security Name': 'name',
            'Listing Exchange': 'exchange',
            'Market Category': 'category',
            'ETF': 'isETF',   
            'Round Lot Size': 'lotSize',
            'Test Issue': 'isTest',
            'Financial Status': 'status',
            'CQS Symbol': 'cqsSymbol',
            'NextShares': 'nextShares'
        })
    symbols_data = symbols_data.to_dict(orient='records')
    print(f'Dados de {len(symbols_data)} instrumentos carregados.')

def find_symbol(symbol):
    for index, item in enumerate(symbols_data):
        if item['symbol'] == symbol:
            return index
    return None

@app.route('/api/symbols', methods=['GET', 'POST'])
def symbols():
    if request.method == 'GET':
        page = int(request.args.get("page", 1))
        per_page = int(request.args.get("per_page", 10))
        start = (page - 1) * per_page
        end = start + per_page
        paginated_symbols = symbols_data[start:end]
        return jsonify(paginated_symbols)
    elif request.method == 'POST':
        new_symbol = request.json
        symbol = new_symbol['symbol']
        if find_symbol(symbol) is not None:
            return make_response(jsonify({'error': 'O instrumento já existe.'}), 400)
        else:
            symbols_data.append(new_symbol)
            return make_response(jsonify({'message': f'Instrumento adicionado.'}), 201)

@app.route('/api/symbols/<symbol>', methods=['GET', 'PUT', 'DELETE'])
def symbol(symbol):
    symbol_index = find_symbol(symbol)

    if symbol_index is None:
        return make_response(jsonify({'error': 'Instrumento não encontrado.'}), 404)

    if request.method == 'GET':
        return jsonify(symbols_data[symbol_index])

    elif request.method == 'PUT':
        updated_symbol = request.json
        symbols_data[symbol_index] = updated_symbol
        return jsonify({'message': 'Instrumento atualizado.'})

    elif request.method == 'DELETE':
        del symbols_data[symbol_index]
        return jsonify({'message': 'Instrumento removido.'})

if __name__ == '__main__':
    print('Iniciando a API...')
    load_data()
    app.run(debug=True)
