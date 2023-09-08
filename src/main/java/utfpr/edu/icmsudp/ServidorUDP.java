package utfpr.edu.icmsudp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class ServidorUDP {
    
    private DatagramSocket ds;
    private byte[] buffer = new byte[10000];
    
    public ServidorUDP(DatagramSocket ds){
        this.ds= ds;
    }
    
    public Pessoa dadosPessoa() throws IOException, ClassNotFoundException {
        
        while(true){
            DatagramPacket datagram = new DatagramPacket(buffer, buffer.length);
            ds.receive(datagram);

            //Converte os bytes recebidos para o objeto
            byte[] bytesRecebidos = datagram.getData();
            ByteArrayInputStream streamByte = new ByteArrayInputStream(bytesRecebidos);
            ObjectInputStream objetoStream = new ObjectInputStream(streamByte);
            
            //Desserializando o objeto
            Pessoa pessoaRecebida = (Pessoa) objetoStream.readObject();
            objetoStream.close();
            
            System.out.println("Dados recebidos");
            System.out.println("Nome: " + pessoaRecebida.getNome());
            System.out.println("Idade: " + pessoaRecebida.getIdade());
            System.out.println("Altura: " + pessoaRecebida.getAltura());
            System.out.println("Peso: " + pessoaRecebida.getPeso());
            String imc = String.format("%.2f", pessoaRecebida.getPeso() / (pessoaRecebida.getAltura() * pessoaRecebida.getAltura()));
            imc = imc.replace(',', '.');
            System.out.println("IMC: " + imc);

            byte[] sendData = imc.getBytes();

            DatagramPacket respostaDatagram = new DatagramPacket(sendData, sendData.length, datagram.getAddress(), datagram.getPort());
            ds.send(respostaDatagram);
                    
            bytesRecebidos = new byte[10000];
            
            return pessoaRecebida;
        }
        
    }

    public static void main(String[] args) throws SocketException, ClassNotFoundException, IOException {
        
        DatagramSocket ds = new DatagramSocket(8081);
        
        ServidorUDP server = new ServidorUDP(ds);
        
        System.out.println("Servidor subiu");
        
        server.dadosPessoa();
        
    }
}
