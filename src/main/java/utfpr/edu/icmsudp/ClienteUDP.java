package utfpr.edu.icmsudp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ClienteUDP {
    
    private DatagramSocket ds;//socket para estabelecer a comunicação
    private byte[] buffer;//array de bytes que levará a mensagem cerealizada
    private InetAddress ip;//Endereço do client/servidor

    public ClienteUDP(DatagramSocket ds, InetAddress ip) {
        this.ds = ds;
        this.ip = ip;
    }
    
    public Pessoa dadosPessoa() throws IOException, ClassNotFoundException {
       
        Pessoa pessoa = new Pessoa();
        pessoa.setNome("Paulo");
        pessoa.setIdade(26);
        pessoa.setAltura(1.70);
        pessoa.setPeso(69.50);
        
        while(true){
            
            //Serializando o objeto em bytes
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            ObjectOutputStream pessoaStream = new ObjectOutputStream(byteStream);
            pessoaStream.writeObject(pessoa);
            pessoaStream.flush();
            byte[] sendData = byteStream.toByteArray();
            pessoaStream.close();
            
            //Enviando os bytes por DatagramPacket
            DatagramPacket pacoteEnvio = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("localhost"), 8081);
            ds.send(pacoteEnvio);
            
            //Limpar buffer
            sendData = new byte[1024];
            
            
            //Receber a resposta do servidor
            DatagramPacket pacoteRecebido = new DatagramPacket(sendData, sendData.length);
            ds.receive(pacoteRecebido);
            
            String imc = new String (pacoteRecebido.getData(), 0, pacoteRecebido.getLength(), "UTF-8");
            double imcConvertido = Double.parseDouble(imc);
            pessoa.setImc(imcConvertido);
            
            System.out.println(pessoa.getNome());
            System.out.println(pessoa.getIdade());
            System.out.println(pessoa.getAltura());
            System.out.println("ICM calculado");
            System.out.println(pessoa.getImc());
            
            
            
            //ds.close();
            
            return pessoa;
        }
        
    }
        

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        DatagramSocket ds = new DatagramSocket();
        InetAddress ip = InetAddress.getByName("localhost");
        
        ClienteUDP client = new ClienteUDP(ds, ip);
        
        client.dadosPessoa();
        
    }
    
}
