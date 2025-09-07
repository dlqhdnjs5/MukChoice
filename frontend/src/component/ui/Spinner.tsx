import '../../styles/Spinner.css';

interface Props {
    message?: string;
}

const Spinner = (props: Props) => (
    <div className="lp-spinner-overlay">
        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
            <div className="lp-spinner" />
            <div className="text-[#ff5e62]" style={{ marginTop: 16, fontWeight: 'bold', textAlign: 'center' }}>
                {props.message}
            </div>
        </div>
    </div>
);

export default Spinner;

